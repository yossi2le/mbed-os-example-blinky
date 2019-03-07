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
#include "bootloader_cliapp_setup.h"
#include "bootloader_cliapp_platform.h"
#include "bootloader_cliapp_cmd.h"


//#include "SDBlockDevice.h"

/* initialise sd card blockdevice */
//SDBlockDevice sd(P12_0, P12_1, P12_2, P12_3);


int main(void)
{
    //printf("\n OFR_DBG Starting cliapp!!!\n");

    // setup the mbed-client-cli test framework
    bootloader_cliapp_setup();

    // setup the hw platform
    bootloader_cliapp_platform_setup();

    // Setup cliapp command
    bootloader_cliapp_cmd_setup();

    // Start running the test framework will block
    // forever
    bootloader_cliapp_start();  
}
