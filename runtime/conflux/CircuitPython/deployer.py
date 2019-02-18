####
# Copyright (c) 2019 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import os, shutil

def deploy(cp_dir_name, cp_path="/media/pi"):
  cp_dev_path = cp_path + "/" + cp_dir_name
  if os.path.isdir(cp_dev_path):
    print("Found CircuitPython device at: " + cp_dev_path)
    if os.path.exists(cp_dev_path + "/boot_out.txt"):
      print("Dumping boot_out.txt contents:")
      with open(cp_dev_path + "/boot_out.txt", 'r') as boot_out_file:
        print boot_out_file.read()
    if os.path.exists(cp_dev_path + "/cascadia.py"):
      print("Removing old cascadia.py from " + cp_dir_name)
      os.remove(cp_dev_path + "/cascadia.py")
    shutil.copy("cascadia.py", cp_dev_path)
    print("Copied latest cascadia.py to " + cp_dir_name)
    print("~~~")
  else:
    print("No CircuitPython device found at: " + cp_dev_path)

deploy("CIRCUITPY")
deploy("CIRCUITPY1")
deploy("CIRCUITPY2")
deploy("CIRCUITPY3")

