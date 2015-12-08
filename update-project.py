import os
vars = {
        'ptr_lib_version': '1.0.11',
        'ptr_lib_stable_version': '1.0.11',
        'cube_sdk_version': '1.0.44.39-SNAPSHOT'
        }

files = [
        'README.md',
        'README-cn.md',
        'ptr-lib/pom.xml',
        'ptr-lib/gradle.properties',
        'ptr-demo/pom.xml',
        'ptr-demo/build.gradle',
        ]

current_dir = os.path.dirname(os.path.realpath(__file__))
print current_dir
src_dir = current_dir + '/template/'
dst_dir = current_dir + '/'

def update_var_for_file(file, vars):
    src_file = src_dir + file
    dst_file = dst_dir + file
    print "update_var_for_file: %s => %s" % (src_file, dst_file)
    infile = open(src_file)
    outfile = open(dst_file, 'w')

    for line in infile:
        for src, target in vars.iteritems():
            line = line.replace(src, target)
        outfile.write(line)
    infile.close()
    outfile.close()

real_vars = {}
for src, target in vars.iteritems():
    real_vars['{' + src + '}'] = target

for f in files:
    update_var_for_file(f, real_vars)
