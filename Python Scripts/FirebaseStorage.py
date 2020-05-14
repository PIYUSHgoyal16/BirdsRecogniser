
# -*- coding: utf-8 -*-
"""
Created on Sat Feb  8 14:19:55 2020

@author: Piyush Goyal
"""

from google.cloud import storage
from firebase import firebase
import urllib
import numpy as np
import cv2

firebase = firebase.FirebaseApplication('https://takepicture-8a86e.firebaseio.com/')
client = storage.Client.from_service_account_json("TakePicture-bd870f190cee.json")
buckets = list(client.list_buckets())
print(buckets)
bucket = client.get_bucket("takepicture-8a86e.appspot.com")
"""
#Code for uploading
"""
imageBlob = bucket.blob("uploads")
imagePath = "C:/Users/Piyush Goyal/Downloads/wall.jpg"
imageBlob = bucket.blob("wall")
imageBlob.upload_from_filename(imagePath)

"""Code for retriving"""
#gs://takepicture-8a86e.appspot.com/wall
imageBlob = bucket.blob("wall")
print(imageBlob)
url = imageBlob.public_url
print(url)
req= urllib.request.urlopen(url)
print(req)
arr = np.asarray(bytearray(req.read()), dtype=np.uint8)
print(arr)
img = cv2.imdecode(arr,-1)
cv2.imshow("image",img)
cv2.waitKey(0)
