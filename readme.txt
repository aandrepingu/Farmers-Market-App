To compile and run this application, first navigate to the hw5 directory, or whatever directory is the parent of src, scripts, docs, etc.

Then:

On Windows, execute the command: .\scripts\run_windows.cmd <path to javafx jars> <path to MySQL connector jar>
On Linux, execute the command: . ./scripts/run_linux.sh <path to javafx jars> <path to MySQL connector jar>

Both of these will compile and run the program. There is also a directory bin which contains the .class files for the program after compilation
and serves as the classpath for the application. 

To generate javadoc:

On Windows, execute the command: .\scripts\javadoc_windows.cmd "<path to javafx jars>" "<path to MySQL connector jar>"
On Linux, execute the command: . ./scripts/javadoc_linux.sh "<path to javafx jars>" "<path to MySQL connector jar>"


For all scripts, sure to include the correct absolute path to the folder containing your javafx jars and the path to your MySQL connector jar, and please escape all spaces in directory names on linux (directory\ with\ space), or enclose all paths in quotes.

For Linux, the . in front of the scripts is important. Don't forget it. 


To load up the database please refer to manuals/install.txt for instructions/details. 


To develop and test my solution I used Windows 11 and powershell commands.
