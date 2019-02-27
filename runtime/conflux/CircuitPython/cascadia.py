####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import board

import os

def init():
  if "_cpDevice" in globals():
    global _cpDevice
    print("Returning existing device: " + _cpDevice.get_type())
    return _cpDevice
  #
  machine=os.uname().machine
  #
  if "Gemma" in machine:
    import Gemma as dev
    global _cpDevice
    _cpDevice=dev.Gemma()
    print("Initialized Gemma device!")
  elif "HalloWing" in machine:
    import HalloWing as dev
    global _cpDevice
    _cpDevice=dev.HalloWing()
    print("Initialized HalloWing device!")
  elif "Trellis" in machine:
    import NeoTrellis as dev
    global _cpDevice
    _cpDevice=dev.NeoTrellis()
    print("Initialized NeoTrellis device!")
  else:
    import CP_Device as dev
    global _cpDevice
    _cpDevice=dev.CP_Device()
    print("Device not recognized: " + machine)
  return _cpDevice


