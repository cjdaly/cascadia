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
    #
    deploy_file("cascadia.py", cp_dir_name, cp_path);
    deploy_file("CP_Device.py", cp_dir_name, cp_path);
    #
    if os.path.exists(cp_dev_path + "/boot_out.txt"):
      with open(cp_dev_path + "/boot_out.txt", 'r') as boot_out_file:
        boot_out_text = boot_out_file.read().strip()
        detect_device_type(boot_out_text, cp_dir_name, cp_path)
    print("~~~")
  else:
    print("No CircuitPython device found at: " + cp_dev_path)

def detect_device_type(boot_out_text, cp_dir_name, cp_path):
  if (boot_out_text.startswith("Adafruit CircuitPython")):
    print("Detecting Adafruit CircuitPython device")
    if "Gemma" in boot_out_text:
      deploy_file("Gemma.py", cp_dir_name, cp_path)
    elif "HalloWing" in boot_out_text:
      deploy_file("HalloWing.py", cp_dir_name, cp_path)
    elif "Trellis" in boot_out_text:
      deploy_file("NeoTrellis.py", cp_dir_name, cp_path)
    else:
      print("Unknown device: " + boot_out_text)
  else:
    print("Unrecognized boot_out.txt: " + boot_out_text)


def deploy_file(deploy_file_name, cp_dir_name, cp_path):
  cp_dev_path = cp_path + "/" + cp_dir_name
  if os.path.exists(cp_dev_path + "/" + deploy_file_name):
    print("Removing old " + deploy_file_name + " from " + cp_dir_name)
    os.remove(cp_dev_path + "/" + deploy_file_name)
  shutil.copy(deploy_file_name, cp_dev_path)
  print("Copied latest " + deploy_file_name + " to " + cp_dir_name)



deploy("CIRCUITPY")
deploy("CIRCUITPY1")
deploy("CIRCUITPY2")
deploy("CIRCUITPY3")

