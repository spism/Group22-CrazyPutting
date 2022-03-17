To start the physics engine, in the main method, when initializing the "test" object, in the constructor parameters define a filepath for an input file.  Then, call the method runSimulation with parameters initial x, initial y (starting points of balls), initial speed X, initial speed Y (starting put of ball in x and y directions) in the same order as mentioned.

To start the game:
First, please change all the filenames of those Textures and 3dModel inside "core\src\com\mga1\game\modgolf.java" to their absolute path.
Then, please change the filename of the input file inside "core\src\com\mga1\game\PhysicsEngine.java" to its abosulute path.
Finally, run the main method inside "desktop\src\com\mga1\game\DesktopLauncher.java".

 ------------------------------------------------------------------ Input file format ------------------------------------------------------------------
The golf parameters must be defined in the following order, where each individual character/logical string of characters are separated by spaces:
x0 = [x0]
y0 = [y0]
xt = [xt]
yt = [yt]
r = [r]
muk = [muk]
mus = [mus]
heightProfile = [heightProfile]
sandPitX = [sandPitX]
sandPitY = [sandPitY]
muks = [muks]
muss = [muss]
  
Wherever an unknown variable is surrounded by "[]" brackets, any user input of any number type (except imaginary) is accepted, as long as the number fits into a double variable.  The only exceptions are:
 - heightProfile, for which a sample input can be ( 0.5 * ( 0.9 + sin ( ( ( x - y ) / 7 ) ) ) )
   -> where there must be a pair of brackets surrounding each operation (x - y), ((x-y)/7), sin(((x-y)/7)), etc.
   -> the height profile accepts the operations +, -, *, /, ^, sin, cos, sqrt, abs, logb10 (logarithm with base 10), logbe (natural logarithm) in that exact format
 - sandPitX and sandPitY, where a sample input could look like 1 < x < 3 (i.e. any two numbers, between which the unknown value will be sandwiched)
   -> Sand pits are also OPTIONAL, but everything up to and including the height profile is necessary
   
The slopes of the plane in directions (x,y) are calculated as tiny differences in height (getHeight(x,y)) over tiny differences of (x,y) respectively, found the errors marginal.
