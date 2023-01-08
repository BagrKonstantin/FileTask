*Filer* is an amazing app for people who want to deal with
merging files, but don't want to catch infinite loop due 
to cycle dependency or catch IOException due to incorrect 
filepath or lack of writing or reading rights.

We solve these annoying problems with our app.

You should enter absolute path to the directory with 
files you want to process and then enter path to file 
you want to write merged data in.

Paths in files you want to process should be local.
For example if you have folders 
Folder_1 and Folder_2 in  C:\programming\Java\FileTask\test
And file_1.1 in Folder_1 requires file_2.1 in Folder_2
require ‘Folder_2/file_2.1’ should be written in file_1.1

Be careful with quotation marks! They should be same as in ReadMe.
Break a leg!