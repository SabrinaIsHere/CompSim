-- This is in a seperate file so it's easy to find and doesn't clutter anything

function indent(str)
	return globals.htmlTab .. globals.htmlTab .. str
end

msgs = {
	cd="coming soon!",

	ls="Usage: ls [OPTION]... [FILE]...\n" ..
	"List information about the FILEs (working/current directory by default)\n\n" ..
	"Long arguments are not available\n" ..
	indent("-a, all: do not ignore hidden entries\n") ..
	indent("-l, long: give more information in a longer format"),

	mkdir="Usage: mkdir [OPTION]... [DIRECTORY]...\n" ..
	"Creates a directory at [DIRECTORY]\n\n" ..
	"Long arguments are not available\n" ..
	indent("-p, parents: Makes parent directories as needed"),

	cat="Usage: cat [OPTION]... [FILE]...\n" ..
	"Prints the contents of FILE(s)\n\n" .. 
	"When no file is specified recorded output is printed instead.\n\n" ..
	indent("-n, number: numbers the printed lines\n") .. 
	indent("-E, -e, ends: marks line breaks with '$'"),

	cp="Usage: cp [OPTION]... SOURCE DEST\n" .. globals.htmlTab .. htmlSpace .. 
	"or:" .. globals.htmlTab .. "cp [OPTION]... SOURCE... DEST\n" ..
	"Copy SOURCE to DEST or multiple SOURCE(s) to DIRECTORY\n\n" ..
	"Does not delete any data by default\n" ..
	indent("-f, force: ignores warnings and overwrites existing files and directories. WILL delete data.\n") ..
	indent("-R, -r, recursive: recursively copies all children of copied directories"),

	mv="Usage: mv [OPTION]... [SOURCE] [DEST]\n" .. htmlSpace .. htmlSpace .. htmlSpace ..
	"or:" .. globals.htmlTab .. "mv [OPTION]... SOURCE... DIRECTORY...\n" ..
	"Rename SOURCE to DEST, or move SOURCE(s) to DIRECTORY\n\n" ..
	"If any error occurs during move it will immediately stop and no source files will be lost\n" ..
	indent("-f, force: ignores warnings and overwrites existing files and directories. WILL delete data"),

	rm="Usage: rm [OPTION]... [FILE]...\n" ..
	"Removes (deletes) FILE(s)\n\n" ..
	"Will continue deleting listed files after an error\n" ..
	indent("-f, force: ignores warnings and does not print errors"),

	rmdir="Usage: rmdir [OPTION]... [DIRECTORY]...\n" ..
	"Removes (deletes) DIRECTORY(ies)\n\n" ..
	"Will continue deleting listed directories after an error\n" ..
	indent("-f, force: ignores warnings and does not print errors")
}

setmetatable(msgs, {
	__index = function(t, key)
		return "Help message for '" .. key .. "' not found."
	end,
})

globals["help_msgs"] = msgs