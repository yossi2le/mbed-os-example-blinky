// ----------------------------------------------------------------------------
// Copyright 2018 ARM Ltd.
//
// SPDX-License-Identifier: Apache-2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ----------------------------------------------------------------------------

#include "bootloader_cliapp_cmd.h"
#include "bootloader_cliapp_utils.h"
#include "mbed-client-cli/ns_cmdline.h"
#include "mbed.h"
#include "mbedtls/sha256.h"
#include "update-client-paal/arm_uc_paal_update.h"
#include "cmsis_os2.h"
#include <stdint.h>

#define CLEAR_EVENT 0xffffffff
uint32_t event_callback = CLEAR_EVENT;

#define BUFFER_SIZE 1024
static uint8_t buffer_array[BUFFER_SIZE] = {0};
static arm_uc_buffer_t buffer = {
    .size_max = BUFFER_SIZE,
    .size = 0,
    .ptr = buffer_array
};

static Ticker reboot_delay_ticker;
static uint32_t firmware_offset = 0;
static uint32_t firmware_location = 0;

/**
 * @brief Given a firmware blob store it in firmware storage
 * @detail usage: "firmware <blob> <blob_hash>". The blob and hash
 *         are base64 endcoded. The hash is a sha256 hash of the
 *         encoded blob.
 */
static int bootloader_cliapp_cmd_firmware_cb(int argc, char *argv[])
{
   // printf("\n OFR_DBG CMD FW CB!!!\n");
    int ret = mbedtls_sha256_ret((const unsigned char*) argv[1],
                                 strlen(argv[1]),
                                 buffer_array, false);

    uint32_t output_len = 0;
    uint8_t* output = buffer_array+32;
    bootloader_cliapp_utils_base64_decode((const uint8_t*)argv[2],
                                          strlen(argv[2]),
                                          buffer_array+32,
                                          &output_len);
    bootloader_cliapp_utils_printhex(output, output_len);

    if (memcmp(buffer_array, output, 32) == 0) {
        cmd_printf("Success\r\n");
        bootloader_cliapp_utils_base64_decode((const uint8_t*) argv[1],
                                              strlen(argv[1]), buffer.ptr,
                                              &(buffer.size));

        arm_uc_error_t ucp_status = ARM_UCP_Write(firmware_location, firmware_offset, &buffer);
        /* wait for event if call was accepted */
        if (ucp_status.error == ERR_NONE)
        {
            while (event_callback == CLEAR_EVENT)
            {
                __WFI();
            }

            if (event_callback == ARM_UC_PAAL_EVENT_WRITE_DONE)
            {
                firmware_offset += buffer.size;
            }
        }
        else
        {
            cmd_printf("ARM_UCP_Prepare failed\r\n");
            return 1;
        }
    } else {
        cmd_printf("Fail\r\n");
        return 1;
    }

    return 0;
}

/**
 * @brief Call ARM_UCP_Finalize
 */
static int bootloader_cliapp_cmd_firmware_finish_cb(int argc, char *argv[])
{
    //printf("\n OFR_DBG CMD FW Finish CB!!!\n");
    arm_uc_error_t ucp_status = ARM_UCP_Finalize(firmware_location);
    /* wait for event if call was accepted */
    if (ucp_status.error == ERR_NONE)
    {
        while (event_callback == CLEAR_EVENT)
        {
            __WFI();
        }
    }
    else
    {
        cmd_printf("ARM_UCP_Finalize failed\r\n");
        return 1;
    }

    return 0;
}

/**
 * @brief Prepare a firmware storage slot
 * @detail usage: "firmware_prepare -s <slot_id> -l <fw_length>
 *         -t <fw_timestamp> -h <fw_hash>". The hash is a sha256
 *         hash of the entire firmware, base64 encoded. This
 *         function calls ARM_UCP_Prepare.
 */
static int bootloader_cliapp_cmd_firmware_prepare_cb(int argc, char *argv[])
{
//    printf("\n OFR_DBG CMD FW Prepare CB!!!\n");
    int32_t slot = 0;
    int64_t timestamp = 0;
    int32_t length = 0;
    uint8_t* hash_str = NULL;
    uint8_t hash[32] = {0};

    if (!( cmd_parameter_int(argc, argv, "-s", &slot) && \
           cmd_parameter_int(argc, argv, "-l", &length) && \
           cmd_parameter_timestamp(argc, argv, "-t", &timestamp) && \
           cmd_parameter_val(argc, argv, "-h", (char**) &hash_str)) ) {
        return 1;
    }

    arm_uc_firmware_details_t details = { 0 };
    details.version = timestamp;
    details.size = length;
    uint32_t output_len = 0;
    firmware_location = slot;
    firmware_offset = 0;

    bootloader_cliapp_utils_base64_decode((const uint8_t*) hash_str,
                                          strlen((const char*)hash_str),
                                          details.hash, &output_len);
    bootloader_cliapp_utils_printhex(details.hash, 32);

    arm_uc_buffer_t temp_buffer = {
        .size_max = BUFFER_SIZE,
        .size     = 0,
        .ptr      = buffer_array
    };

    cmd_printf("ARM_UCP_Prepare\r\n");
    event_callback = CLEAR_EVENT;
    arm_uc_error_t ucp_status = ARM_UCP_Prepare(slot, &details, &temp_buffer);

    /* wait for event if call was accepted */
    if (ucp_status.error == ERR_NONE)
    {
        while (event_callback == CLEAR_EVENT)
        {
            __WFI();
        }
    }
    else
    {
        cmd_printf("ARM_UCP_Prepare failed\r\n");
        return 1;
    }

    return 0;
}

/**
 * @brief Reboot the device
 * @detail Use a ticker to deplay reboot for 1 second.
 *         this allows the function to return first and
 *         return success to the host before reboot.
 */
static int bootloader_cliapp_cmd_reboot_cb(int argc, char *argv[])
{
//    printf("\n OFR_DBG CMD FW Reboot!!!\n");
    reboot_delay_ticker.attach(&NVIC_SystemReset, 1);
    return 0;
}

/**
 * @brief Register all the commands with mbed-client-cli framework
 */
void bootloader_cliapp_cmd_setup()
{
//    printf("\n OFR_DBG CMD FW Setup!!!\n");
    // Add commands
    cmd_add("firmware", &bootloader_cliapp_cmd_firmware_cb, NULL, NULL);
    cmd_add("firmware_prepare", &bootloader_cliapp_cmd_firmware_prepare_cb, NULL, NULL);
    cmd_add("firmware_finish", &bootloader_cliapp_cmd_firmware_finish_cb, NULL, NULL);
    cmd_add("reboot", &bootloader_cliapp_cmd_reboot_cb, NULL, NULL);
}
