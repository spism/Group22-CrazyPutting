# Group22-phase-one
Phase 1 of the second project of the first year.

 -------------------- Input file format --------------------
The golf parameters must be defined in the following order, where each individual character/logical string of characters are separated by spaces:
x0 = <x0>
y0 = <y0>
xt = <xt>
yt = <yt>
r = <r>
muk = <muk>
mus = <mus>
heightProfile = <heightProfile>
sandPitX = <sandPitX>
sandPitY = <sandPitY>
muks = <muks>
muss = <muss>
  
Wherever an unknown variable is surrounded by "<>" brackets, any user input of number type is accepted.  The only exceptions are:
 - heightProfile, for which a sample input can be ( 0.5 * ( 0.9 + sin ( ( ( x - y ) / 7 ) ) ) )
   -> where there must be a pair of brackets surrounding each operation (x - y), ((x-y)/7), sin(((x-y)/7)), etc.
 - sandPitX and sandPitY, where a sample input could look like 1 < x < 3 (i.e. any two numbers, between which the x value will be sandwiched

