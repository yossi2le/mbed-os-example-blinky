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

#include "bootloader_cliapp_platform.h"
#include "update-client-paal/arm_uc_paal_update.h"
#include "mbed-client-cli/ns_cmdline.h"
#define __STDC_FORMAT_MACROS
#include <inttypes.h>
#include <stdint.h>

#ifdef MBED_CLOUD_CLIENT_UPDATE_STORAGE
extern ARM_UC_PAAL_UPDATE MBED_CLOUD_CLIENT_UPDATE_STORAGE;
#else
#error Update client storage must be defined in user configuration file
#endif

#if MBED_CLOUD_CLIENT_UPDATE_STORAGE == ARM_UCP_FLASHIAP_BLOCKDEVICE
#include "SDBlockDevice.h"

/* initialise sd card blockdevice */
#if defined(MBED_CONF_APP_SPI_MOSI) && defined(MBED_CONF_APP_SPI_MISO) && \
    defined(MBED_CONF_APP_SPI_CLK)  && defined(MBED_CONF_APP_SPI_CS)
SDBlockDevice sd(MBED_CONF_APP_SPI_MOSI, MBED_CONF_APP_SPI_MISO,
                 MBED_CONF_APP_SPI_CLK,  MBED_CONF_APP_SPI_CS);
#else
SDBlockDevice sd(MBED_CONF_SD_SPI_MOSI, MBED_CONF_SD_SPI_MISO,
                 MBED_CONF_SD_SPI_CLK,  MBED_CONF_SD_SPI_CS);
#endif

BlockDevice* arm_uc_blockdevice = &sd;
#endif

extern uint32_t event_callback;

/**
 * @brief Event handler for events from the PAAL implementation
 */
static void bootloader_cliapp_cmd_ucp_event_handler(uint32_t event)
{
    cmd_printf("event: %" PRIx32, event);

    event_callback = event;
}

/**
 * @brief Set up hardware platform for running bootloader tests
 * @detail Set up firmware storage for running bootloader tests
 *
 * @return -1 on error, 0 on success.
 */
int8_t bootloader_cliapp_platform_setup()
{
    int8_t retval = -1;

    // Set PAAL Update implementation before initializing
    ARM_UCP_SetPAALUpdate(&MBED_CLOUD_CLIENT_UPDATE_STORAGE);

    // Initialize PAAL implementation
    arm_uc_error_t ucp_result = ARM_UCP_Initialize(bootloader_cliapp_cmd_ucp_event_handler);
    if (ucp_result.error != ERR_NONE) {
        retval = 0;
    }

    return retval;
}
