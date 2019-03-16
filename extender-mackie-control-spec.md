# X-Touch Extender Sysex Specification

### Name (Sysex)

**Variables**

Header - `00 00 66 15 12`

Position Line 1 - `00, 07, 0e, 15, 1c, 23, 2a, 31`

Position Line 2 - `38, 3f, 46, 4d, 54, 5b, 62, 69`

Name - 7 Characters

**Usage**

`F0 <header> <position> <name> F7`

**Examples**

`F0 00 00 66 15 12 00 54 65 73 74 20 20 20 F7` - Set first top to test

`F0 00 00 66 15 12 00 20 20 20 20 20 20 20 F7` - Clear first top

### Set Display
```
F0 00 00 66 15 12 00 54 65 73 74 20 20 31 F7
F0 00 00 66 15 12 07 54 65 73 74 20 20 32 F7
F0 00 00 66 15 12 0e 54 65 73 74 20 20 33 F7
F0 00 00 66 15 12 15 54 65 73 74 20 20 34 F7
```
```
F0 00 00 66 15 12 1c 54 65 73 74 20 20 35 F7
F0 00 00 66 15 12 23 54 65 73 74 20 20 36 F7
F0 00 00 66 15 12 2a 54 65 73 74 20 20 37 F7
F0 00 00 66 15 12 31 54 65 73 74 20 20 38 F7
```
```
F0 00 00 66 15 12 38 54 65 73 74 20 20 39 F7
F0 00 00 66 15 12 3f 54 65 73 74 20 20 3a F7
F0 00 00 66 15 12 46 54 65 73 74 20 20 3b F7
F0 00 00 66 15 12 4d 54 65 73 74 20 20 3c F7
```
```
F0 00 00 66 15 12 54 54 65 73 74 20 20 3d F7
F0 00 00 66 15 12 5b 54 65 73 74 20 20 3e F7
F0 00 00 66 15 12 62 54 65 73 74 20 20 3f F7
F0 00 00 66 15 12 69 54 65 73 74 20 20 40 F7
```

### Volume (Pitch Bend)

Volume is set using the "Pitch Bend" MIDI message.
The channel corresponds to the strip to adjust.

The value is set from 0 to 16,000 using the course and fine bytes.

**Variables**

Strip ID - `E0` to `E7`

Fine - `00` to `7C`
Course - `00` to `7F`

**Usage**

`<strip-id> <fine> <course>`

**Examples**

`E0 00 00` (Strip 1 Min)

`E0 7C 7F` (Strip 1 Max)

