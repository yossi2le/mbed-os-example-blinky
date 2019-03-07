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

#include "mbed.h"
#include "bootloader_cliapp_utils.h"
#include "mbed-client-cli/ns_cmdline.h"
#include <stdint.h>

/**
 * @brief Print data in hex format
 *
 * @param input Pointer to binary data
 * @param input_len length of the data to be printed
 */
void bootloader_cliapp_utils_printhex(const uint8_t* input,
                                      uint32_t input_len)
{
    for (uint32_t i = 0; i < input_len; i++)
    {
        cmd_printf("%02x", input[i]);
    }
    cmd_printf("\r\n");
}

/**
 * @brief Decode base64-encoded data.
 *
 * @param input Pointer to base64-encoded data.
 * @param input_len length of input data
 * @param output Pointer to buffer which will be filled with
 *               decoded data
 * @param output_len pointer to uint32_t, used to store
 *                   length of decoded data in output.
 */
void bootloader_cliapp_utils_base64_decode(const uint8_t* input,
                                           uint32_t input_len,
                                           uint8_t* output,
                                           uint32_t* output_len)
{
    static const uint8_t base64_table[] = {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3',
      '4', '5', '6', '7', '8', '9', '+', '/'
    };
    static uint8_t* decode_table = (uint8_t*) malloc(256);
    static uint8_t initialised = 0;
    if (!initialised)
    {
        for (int i = 0; i < 64; i++)
        {
            decode_table[(uint8_t) base64_table[i]] = i;
        }
        initialised = 1;
    }

    *output_len = 0;
    uint32_t a = 0;
    for (uint32_t i=0; (i<input_len) && (input[i] != '='); i++)
    {
        uint32_t j = i%4;

        if (j == 0)
        {
            a = 0;
        }

        /* read 4 * 6 = 24 bits input data */
        a |= (decode_table[input[i]] & 0x3f) << (3 - j) * 6;

        /* store 24 = 3 * 8 bits to output */
        if (j > 0)
        {
            output[(*output_len)++] = (a >> ((3 - j) * 8)) & 0xff;
        }
    }
}
