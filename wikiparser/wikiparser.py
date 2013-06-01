from disco.core import Job, result_iterator
import sys
    
class WikiParser(Job):
    partitions = 4
    
    def map(self, row, params):
        results = self._parsePage("<page>" + row[0] + "</page>")
        if results != None:
            for j in results:
                if j != None:
                    yield j[0], j[1]

    @staticmethod
    def regex_reader(item_re_str, fd, size, fname, output_tail=False, read_buffer_size=8192):
        """
        Modified version of disco.worker.classic.func.re_reader - does not throw DataError
        A map reader that uses an arbitrary regular expression to parse the input
        stream.

        :param item_re_str: regular expression for matching input items

        The reader works as follows:

        1. X bytes is read from *fd* and appended to an internal buffer *buf*.
        2. ``m = regexp.match(buf)`` is executed.
        3. If *buf* produces a match, ``m.groups()`` is yielded, which contains an
        input entry for the map function. Step 2. is executed for the remaining
        part of *buf*. If no match is made, go to step 1.
        4. If *fd* is exhausted before *size* bytes have been read,
        and *size* tests ``True``, instead of raising a :class:`disco.error.DataError`,
        it prints something about the error.
        5. When *fd* is exhausted but *buf* contains unmatched bytes, two modes are
        available: If ``output_tail=True``, the remaining *buf* is yielded as is.
        Otherwise, a message is sent that warns about trailing bytes.
        The remaining *buf* is discarded.

        Note that :func:`re_reader` fails if the input streams contains unmatched
        bytes between matched entries.
        Make sure that your *item_re_str* is constructed so that it covers all
        bytes in the input stream.

        :func:`re_reader` provides an easy way to construct parsers for textual
        input streams.
        For instance, the following reader produces full HTML
        documents as input entries::

        def html_reader(fd, size, fname):
        for x in re_reader("<HTML>(.*?)</HTML>", fd, size, fname):
        yield x[0]

        """
        item_re = re.compile(item_re_str)
        buf = b""
        tot = 0
        while True:
            if size:
                r = fd.read(min(read_buffer_size, size - tot))
            else:
                r = fd.read(read_buffer_size)
            tot += len(r)
            buf += r

            m = item_re.match(buf)
            while m:
                yield m.groups()
                buf = buf[m.end():]
                m = item_re.match(buf)

            if not len(r) or (size!=None and tot >= size):
                if size != None and tot < size:
                    print("Truncated input: Expected {0} bytes, got {1}".format(size, tot), fname)
                if len(buf):
                    if output_tail:
                        yield [buf]
                    else:
                        print("Couldn't match the last {0} bytes in {1}. "
                              "Some bytes may be missing from input.".format(len(buf), fname))
                break

    @staticmethod
    def map_reader(fd, size, url, params):
        from wikiparser import WikiParser
        count = 0
        line = fd.readline()
        while "<page>" not in line:
            line = fd.readline()
            count += 1

        fd.seek(0);

        while count > 0:
            fd.readline()
            count -= 1

        reader = WikiParser.regex_reader("\s\s<page>([\s\S]*?)</page>\\n", fd, size, url);
        for row in reader:
                yield row

    def reduce2(self, rows_iter):
        for i in rows_iter:
            yield i

    def _parsePage(self, page):
        from lxml import etree
        from StringIO import StringIO
        import json

        tree = etree.parse(StringIO(page))
        page = {child.tag:child.text for child in tree.iter()}
        title = page['title'][1:]
        try:
            if page['ns'] == '0':
                text = json.loads(page['text'])
                statement = None
                if 'claims' in text:
                    for a in text['claims']:
                        statement =  {i:j for i, j in self._pairwise(a['m'])}
                    if statement != None:
                        try:
                            toyield1 = (title, str(statement['value']))
                            value = str(statement['wikibase-entityid']['numeric-id']) if 'wikibase-entityid' in statement else statement['string']
                            toyield2 = (title, str(statement['value']) + "----" + value)
                        except KeyError:
                            toyield1 = toyield2 = None
                        yield toyield1
                        yield toyield2
        except KeyError:
            pass

    def _pairwise(self, iterable):
        from itertools import izip
        a = iter(iterable)
        return izip(a, a)

if __name__ == '__main__':
    from wikiparser import WikiParser
    
    if(len(sys.argv) < 2):
        print "USAGE: python", sys.argv[0], "<ddfs tag:name> [<output file path>]"
        print "You may omit the output file; it's stdout by default.\n"
        sys.exit()

    job = WikiParser().run(input=[sys.argv[1]])
    outf = sys.stdout if len(sys.argv) < 3 else open(sys.argv[2], "w")
    for a, b in result_iterator(job.wait(show=True)):
        outf.write(a.encode('utf-8') + "," + b.encode('utf-8') + "\n")
