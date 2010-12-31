#!/usr/bin/env python

import cgi

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class MainPage(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()
        self.response.headers['Content-Type'] = 'text/plain'
        if user:
            self.response.out.write('Hello, ' + user.nickname() + '!')
        else:
            self.response.out.write("I don't know you weirdo...")
            
    def post(self):
        user = users.get_current_user()
        self.response.headers['Content-Type'] = 'text/plain'
        if user:
            reply = 'Hello, ' + user.nickname() + '! \n'
            reply += 'These are the values I got for the keys I was expecting: \n'
            reply += 'testKey1: ' + cgi.escape(self.request.get('testKey1')) + ' \n'
            reply += 'testKey2: ' + cgi.escape(self.request.get('testKey2')) + ' \n'
            reply += 'This was the complete body of the request: \n'
            reply += cgi.escape(str(self.request.body))
            self.response.out.write(reply)
        else:
            self.response.out.write("I don't know you weirdo, don't POST to me.")

application = webapp.WSGIApplication(
                                     [('/', MainPage)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
