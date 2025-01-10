import glob, os

def get_filepaths(directory):
    """
    This function will generate the file names in a directory 
    tree by walking the tree either top-down or bottom-up. For each 
    directory in the tree rooted at directory top (including top itself), 
    it yields a 3-tuple (dirpath, dirnames, filenames).
    """
    file_paths = []  # List which will store all of the full filepaths.

    # Walk the tree.
    for root, directories, files in os.walk(directory):
        for filename in files:
            # Join the two strings in order to form the full filepath.
            filepath = os.path.join(root, filename)
            file_paths.append(filepath)  # Add it to the list.

    return file_paths  # Self-explanatory.

for file in get_filepaths("src"):
    if not file.endswith(".java"):
        continue

    file = "/home/coderlol/code/Computronics/" + file
    print(file)
    with open(file, "r") as f:
        data = f.read()
        #print(data)
    with open(file, "w") as f:
        # data = data.replace("pl.asie.lib", "org.libreflock.asielib")
        # data = data.replace("pl.asie", "org.libreflock")

        data = data.replace("net.minecraft.util.math.Vec3d", "net.minecraft.util.math.vector.Vector3d")
        data = data.replace("Vec3d", "Vector3d")
        f.write(data)


print("fixed hopefully")
#print(get_filepaths("src"))
