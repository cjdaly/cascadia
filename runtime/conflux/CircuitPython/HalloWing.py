####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import CP_Device as super

import board
import neopixel

class HalloWing(super.CP_Device):
  #
  def __init__(self):
    self._NP_0 = neopixel.NeoPixel(board.NEOPIXEL,1,brightness=0.5)
    #
    self._has_NP_Strip = False
    if self._has_NP_Strip:
      self._NP_Strip = neopixel.NeoPixel(board.EXTERNAL_NEOPIXEL, 20, brightness=0.5)
  
  #
  def has_NeoPixel_strip():
    return self._has_NP_Strip
  
  #
  def RGB0_set(self, r,g,b):
    self._NP_0[0]=(r,g,b)

  #
  def RGB_fill(self, r,g,b):
    if self.has_NeoPixel_strip():
      self._NP_Strip.fill((r,g,b))

  #
  def RGB_range(self, r,g,b, startIndex=0,numPixels=1):
    for i in range(startIndex, startIndex+numPixels):
      self._NP_Strip[i]=(r,g,b)

  #
  def RGB_skip(self, r,g,b, startIndex=0,numPixels=1,skipCount=1):
    for i in range(startIndex, startIndex+numPixels, skipCount):
      self._NP_Strip[i]=(r,g,b)

      