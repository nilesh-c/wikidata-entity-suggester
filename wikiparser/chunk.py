import sys, os
from disco.ddfs import DDFS


if(len(sys.argv) < 2):
    print "USAGE: python", sys.argv[0], "<ddfs tag:name> [<uncompressed wikidata dump file path>]"
    print "You may pipe the dump file contents using lbzcat or bzcat into stdin like:"
    print "lbzcat wikidatawiki-yyyymmdd-pages-meta-current.xml.bz2 | python <ddfs tag:name>\n"
    sys.exit()

tag = (sys.argv[1])
infile = sys.stdin if len(sys.argv) < 3 else sys.argv[2]

getChunk = lambda x : sys.argv[1] + ".chunk" + str(x)
fcount = 1
count = 1
outfile = open(getChunk(fcount), "w")

for i in infile:
    outfile.write(i)
    if '</page>' in i:
        count += 1
        printProgress = True;
    
    if count % 100000 == 0 and printProgress == True:
        sys.stdout.write("\r%s" % "Finished " + str(count) + " pages.")
        sys.stdout.flush()
        printProgress = True if count % 1000000 == 0 else False
        
    if count % 1000000 == 0 and printProgress == True:
        outfile.close()
        DDFS(master="disco://localhost").push(tag, [os.path.abspath(getChunk(fcount))])
        sys.stdout.write("\nChunk " + str(fcount) + " pushed to tag %s on DDFS.\n" % tag)
        sys.stdout.flush()
        os.remove(getChunk(fcount))
        fcount += 1
        outfile = open(getChunk(fcount), "w")
        printProgress = False
        break
        
if len(sys.argv) > 2: infile.close()
