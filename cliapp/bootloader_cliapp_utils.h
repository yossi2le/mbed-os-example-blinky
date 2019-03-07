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

#ifndef BOOTLOADER_CLIAPP_UTILS_H
#define BOOTLOADER_CLIAPP_UTILS_H
#include <stdint.h>

/**
 * @brief Print data in hex format
 *
 * @param input Pointer to binary data
 * @param input_len length of the data to be printed
 */
void bootloader_cliapp_utils_printhex(const uint8_t* input,
                                      uint32_t input_len);

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
                                           uint32_t* output_len);
#endif /* BOOTLOADER_CLIAPP_UTILS_H */
