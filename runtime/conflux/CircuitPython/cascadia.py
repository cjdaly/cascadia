####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import os, board

def RGB0_init():
  machine=os.uname().machine
  if "Gemma" in machine:
    import adafruit_dotstar as dotstar
    global DS_0
    DS_0 = dotstar.DotStar(board.APA102_SCK, board.APA102_MOSI, 1, brightness=0.5)
  elif ("HalloWing" in machine) or ("Trellis" in machine):
    import neopixel
    global NP_0
    NP_0 = neopixel.NeoPixel(board.NEOPIXEL,1,brightness=0.5)

def RGB0_set(r,g,b):
  machine=os.uname().machine
  if "Gemma" in machine:
    global DS_0
    DS_0[0]=[r,g,b]
  elif ("HalloWing" in machine) or ("Trellis" in machine):
    global NP_0
    NP_0[0]=(r,g,b)
