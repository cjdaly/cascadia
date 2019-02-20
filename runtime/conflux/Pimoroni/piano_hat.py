####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import pianohat, sys

key_names = ["C_lo", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C_hi", "Octave_down", "Octave_up", "Instrument"]

pianohat.auto_leds(True)

def handler(key, evt):
  if not evt: # key release
    print("PianoHat key: " + str(key) + "; name: " + key_names[key])

pianohat.on_note(handler)
pianohat.on_octave_up(handler)
pianohat.on_octave_down(handler)
pianohat.on_instrument(handler)

line = sys.stdin.readline()
while line:
  line=line.strip()
  if not line:
    pass # ignore blank input
  elif line.startswith("hi"):
    print("Hello from PianoHat!")
  else:
    print("echo: " + line)
  #
  line = sys.stdin.readline()

