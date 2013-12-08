import cgi
import json

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

def convert_lat(lat):
    return int((lat + 90) * int("FFFFFFFF", 16) / 180 / 10000)

def convert_lon(lon):
    return int((lon + 180) * int("FFFFFFFF", 16) / 360 / 10000)

def gen_geo_index(x,y):
    return '%010d-%010d' % (x, y)

class Keyword(db.Model):
    keyword = db.StringProperty(multiline=False)
    lat = db.IntegerProperty()
    lon = db.IntegerProperty()
    count = db.IntegerProperty()
    geoindex = db.StringProperty(multiline=False)

def increment_counter(key):
    obj = db.get(key)
    obj.count += 1
    obj.put()

class PutPage(webapp.RequestHandler):
    def get(self):
        # Implement
        key = cgi.escape(self.request.get('key'))
        lat = float(cgi.escape(self.request.get('lat')))
        lon = float(cgi.escape(self.request.get('lon')))

        geoindex = gen_geo_index(convert_lon(lon), convert_lat(lat))

        query = db.GqlQuery("SELECT * FROM Keyword WHERE keyword = :1 AND geoindex = :2", key, geoindex)
        keyword = query.get()

        if keyword:
            db.run_in_transaction(increment_counter, keyword.key())
        else:
            keyword = Keyword()
            keyword.keyword = key
            keyword.lat = convert_lat(lat)
            keyword.lon = convert_lon(lon)
            keyword.count = 1
            keyword.geoindex = geoindex
            keyword.put()

        keywords = db.GqlQuery("Select * FROM Keyword WHERE geoindex = :1 ORDER BY count DESC LIMIT 10", geoindex)

        ret_array = []
        for keyword in keywords:
            dict_key = {"keyword": keyword.keyword, "count": keyword.count, "lat": keyword.lat, "lon": keyword.lon}
            ret_array.append(dict_key)

        ret_array = {"catch": ret_array}

        self.response.out.write(json.dumps(ret_array))

class ListPage(webapp.RequestHandler):
    def get(self):
        #implement
        lat = float(cgi.escape(self.request.get('lat')))
        lon = float(cgi.escape(self.request.get('lon')))

        geoindex = gen_geo_index(convert_lon(lon), convert_lat(lat))

        keywords = db.GqlQuery("Select * FROM Keyword WHERE geoindex = :1 ORDER BY count DESC LIMIT 10", geoindex)

        ret_array = []
        for keyword in keywords:
            dict_key = {"keyword": keyword.keyword, "count": keyword.count, "lat": keyword.lat, "lon": keyword.lon}
            ret_array.append(dict_key)

        ret_array = {"catch": ret_array}

        self.response.out.write(json.dumps(ret_array))

application = webapp.WSGIApplication(
    [('/put', PutPage),
     ('/list', ListPage)],
    debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
