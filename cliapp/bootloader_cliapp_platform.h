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

#include <stdint.h>

#ifndef BOOTLOADER_CLIAPP_PLATFORM_H
#define BOOTLOADER_CLIAPP_PLATFORM_H
/**
 * @brief Set up hardware platform for running bootloader tests
 * @detail Set up firmware storage for running bootloader tests
 *
 * @return -1 on error, 0 on success.
 */
int8_t bootloader_cliapp_platform_setup();
#endif /* BOOTLOADER_CLIAPP_PLATFORM_H */
