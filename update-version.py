version_name = '1.0.5'

def replace_file(fname, start, end, replace, line_from = 1, line_to = 0):

    with open(fname) as f:
            content = f.readlines()
    pre = content[0:line_from-1]
    if line_to == 0:
        mid = content
        post = []
    else:
        mid = content[line_from-1:line_to]
        post = content[line_to:]
    pre = ''.join(pre)
    mid = ''.join(mid)
    post = ''.join(post)
    mid = replace_between(mid, start, end, replace)
    content = pre + mid  + post
    with open(fname, "w") as f:
        f.write(content)

def replace_between(str, start, end, replace):
    pos1 = str.find(start);
    pos2 = str.find(end);
    return str[0:pos1 + len(start)] + replace + str[pos2:]

# replace version for pom
replace_file('ptr-lib/pom.xml', '<version>', '</version>', version_name, 12, 16)
replace_file('ptr-demo/pom.xml', '<version>', '</version>', version_name, 36, 41)

# replace version for gradle
replace_file('ptr-lib/gradle.properties', 'VERSION_NAME=', '\n', version_name, 1, 2)

# update version for reade me
offset = 4
replace_file('README.md', '<version>', '</version>', version_name, 53 + offset, 58 + offset)
replace_file('README.md', '<version>', '</version>', version_name, 63 + offset, 68 + offset)
replace_file('README.md', 'in.srain.cube:ultra-ptr:', '@aar', version_name, 73 + offset, 74 + offset)

replace_file('README-cn.md', '<version>', '</version>', version_name, 51 + offset, 56 + offset)
replace_file('README-cn.md', '<version>', '</version>', version_name, 61 + offset, 66 + offset)
replace_file('README-cn.md', 'in.srain.cube:ultra-ptr:', '@aar', version_name, 70 + offset, 72 + offset)
