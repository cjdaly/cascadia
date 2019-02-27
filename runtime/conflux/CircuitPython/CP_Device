####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import os

class CP_Device:
  #
  def __init__(self):
    machine=os.uname().machine
    if "Gemma" in machine:
      self._type="Gemma"
    elif "HalloWing" in machine:
      self._type="HalloWing"
    elif "Trellis" in machine:
      self._type="NeoTrellis"
    else:
      self._type="unknown [" + machine + "]"

  def get_type(self):
    return self._type

  def RGB0_set(self, r,g,b):
    print("RGB0_set not implemented!")

  def RGB_fill(self, r,g,b):
    print("RGB_fill not implemented!")

  def RGB_range(self, r,g,b, startIndex=0,numPixels=1):
    print("RGB_range not implemented!")

  def RGB_skip(self, r,g,b, startIndex=0,numPixels=1,skipCount=1):
    print("RGB_skip not implemented!")


