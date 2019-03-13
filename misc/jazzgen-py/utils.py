# Utility functions and classes

class Enumerate(object):
    "Define an enumeration with optional auto-creation of constants (use lock() to disable)"
    def __init__(self, names=None, locked=False):
        self.names = {}
        self.locked = locked
        if names == None:
            self.last = 0
        else:
            for number, name in enumerate(names.split()):
                setattr(self, name, number)
                self.names[number] = name
            self.last = number
    def getName(self, num):
        "Return the name of an Enumeration constant"
        return self.names[num]
    def getConst(self, name):
        try:
            return [k for k, v in self.names.iteritems() if v == name][0]
        except IndexError:
            return None
    def lock(self):
        self.locked = True
    def unlock(self):
        self.locked = False
    def __getattr__(self, name):
        "Automatically define a constant unless locked"
        if name.startswith('__'):
            return None # reserved method name
        elif self.locked:
            raise Exception("Invalid or missing key: %s" % name)
        else:
            self.last = self.last + 1
            setattr(self, name, self.last)
            self.names[self.last] = name
            return self.last
    def __repr__(self):
        "Printable representation of this enumeration object"
        return "<Enumerate names=%s locked=%s last=%d>" % (repr(self.names), self.locked, self.last)