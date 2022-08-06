indent = globals.help_msgs.indent

globals.help_msgs["list_entries"] = "Usage: list_entries\n" ..
"Lists all entries registered to the database"

globals.help_msgs["add_entry"] = "Usage: add_entry [OPTION]... [NAME]\n" ..
"or: add_entry [OPTION]... [NAME] [DATA_TYPE] [DATA]\n\n" ..
"Valid data types include: bool, int, string. Anything more complex can be done in the shell.\n" ..
indent("-f, force: overwrite existing entries")

globals.help_msgs["remove_entry"] = "Usage: remove_entry [NAME]\n" ..
"This will permenantly delete the entry. It will not be recoverable"