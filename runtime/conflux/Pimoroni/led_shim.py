####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import ledshim, sys

def clear():
  ledshim.clear()
  ledshim.show()

def solid_color(color_name):
  if color_name=="red":
    r,g,b=100,0,0
  elif color_name=="green":
    r,g,b=0,100,0
  elif color_name=="blue":
    r,g,b=0,0,100
  else:
    r,g,b=50,50,50
  #
  for p in range (ledshim.DISPLAY_WIDTH):
    ledshim.set_pixel(p, r, g, b)
  #
  ledshim.show()

line = sys.stdin.readline()
while line:
  line=line.strip()
  if not line:
    # ignore blank input
  elif line.startswith("hi"):
    print("Hello from LedShim!")
  elif line.startswith("clear"):
    clear()
  elif line.startswith("solid:"):
    solid_color(line.split("solid:",1)[1])
  else:
    print("Unrecognized command: " + line)
  #
  line = sys.stdin.readline()

