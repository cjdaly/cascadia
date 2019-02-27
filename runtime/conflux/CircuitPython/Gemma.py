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
import adafruit_dotstar as dotstar

class Gemma(super.CP_Device):
  #
  def __init__(self):
    self._DS_0 = dotstar.DotStar(board.APA102_SCK, board.APA102_MOSI, 1, brightness=0.5)
    
  
  def RGB0_set(self, r,g,b):
    self._DS_0[0]=[r,g,b]



