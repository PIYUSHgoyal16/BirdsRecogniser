#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Feb 10 11:52:10 2020

@author: user
"""
from keras.models import Sequential
from keras.layers import Dense
from keras.models import model_from_json
import numpy as np
import os
from keras.models import load_model
from keras import backend as k 
import sys
import tensorflow
import heapq
from keras.layers import Dense, Dropout, Activation, Flatten
from keras.layers import Conv2D, MaxPooling2D
from keras.utils import np_utils
import math
import librosa
import sys
import numpy as np
import subprocess
import scipy.io.wavfile as wav
import os
import os.path
import json
import uuid
from flask import Flask,request, Response

def softmax(x):
  e_x = np.exp(x - np.max(x))
  return e_x / e_x.sum()

def birdsname(audo_file):
    """convert wav to raw file first"""
    
    sr1,y=wav.read('XC469692-Canada Goose flight call-own (online-audio-converter.com).wav')
    y,sr1=librosa.core.load('XC469692-Canada Goose flight call-own (online-audio-converter.com).wav', sr=sr1, mono=True)
    frameSize = int(math.ceil(20e-3*sr1))
    frameShift = int(math.ceil(frameSize/2))
    r=0
    g=0 
    (c,) = y.shape
    print (c)
    print (frameSize, frameShift)
    numberOfFrames = y.size/frameShift
    numberOfFrames = math.floor(numberOfFrames)
    print (numberOfFrames, frameSize)
    numberOfFrames=int(numberOfFrames)
    raw = np.zeros((numberOfFrames, frameSize))
    a = np.zeros((numberOfFrames, frameSize))
    startIndex = 0;
    for it in range(numberOfFrames):
        if startIndex+frameSize > c:
            print ("working on last frame" )
            continue
        else:
            raw[it,:] = y[startIndex:(startIndex+frameSize)].transpose();            	    startIndex = startIndex + frameShift - 1
    f = open('audio_file.raw', 'w')
    np.savetxt('audio_file.raw',raw,fmt='%f')
    f.close()
    
    """process the file"""
    
    model = Sequential()
    model.add(Conv2D(64, (15, 5), input_shape=(882,15,1), activation="relu"))
    model.add(MaxPooling2D(pool_size=(2, 1), strides=(1,1), padding='valid', data_format=None))
    model.add(Conv2D(64,(15, 5), activation='relu'))
    model.add(MaxPooling2D(pool_size=(2, 1), strides=(1,1), padding='valid', data_format=None))
    model.add(Flatten())
    model.add(Dense(128, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(50, activation='softmax'))
    model.load_weights('best_cnn_50.h5')
    model.compile(loss='categorical_crossentropy', optimizer='rmsprop', metrics=['accuracy'])
    file_specified = "audio_file.raw"
    print(file_specified)
    i=0
    example = np.loadtxt(file_specified)
    rows, cols = example.shape
    context = np.zeros((rows-14,15*cols))
    while i <= (rows - 15):
      ex = example[i:i+15,:].ravel()
      ex = np.reshape(ex,(1,ex.shape[0]))
      context[i:i+1,:] = ex
      i += 1
    print ("No. of context windows: %d" %i)
    context=context.reshape(-1,882,15,1)
    prediction=model.predict(context)
    sum1=np.sum(prediction, axis=0)
    a=np.asarray(sum1)
    labels=heapq.nlargest(5, range(len(a)), a.take)
    label= np.argmax(np.asarray(sum1))
    confidence_matrix = softmax(sum1)
    confidence_matrix = confidence_matrix*100
    file_label = label
    list1=['African_pigtal','Bald_eagle','Bee_eater','Black_woodpecker','Black_throated_tit','Blackandyellow_grosbeak','Blackcrested_tit','California_thrasher','Canada_goose','Carion_crow','Cassins','Chestnutcrowned_laughingthrush',
    'Coat_tit','Common_Pheasant','Common_Redshank','Common_blkbrd','Common_nightingate','Duncock','Easter_phoebe','Eurasian_blackcap','Eurasian_bullfinch','Eurasian_collared_dove','Eurasian_nuthatch','Eurasian_treecreeper',
    'European_turtle_dove','Fish_crow','Flamingo','Garden_warbler','Goldcrest','Golden_bushrobin','Great_barbet','Grey_bellied_cuckoo','Grey_bushchat','Greyhooded_warbler','Greywinged_blackbird','Himalayan_monal',
    'Largebilled_crow','Lesser_cuckoo','Orangeflanked_bushrobin','Oriental_cuckoo','Palerumped_warbler','Rock_bunting','Rufous_gorgetted_flycatcher','Rufousbellied_niltava','Russetbacked_sparrow','Spotted_nutcracker',
    'Streaked_laughingthrush','Western_tragopan','Whitecheeked_nuthatch','Yellowbellied_fantail']
    list1.sort()
    val=[]
    print("Predicted Class Label: ",'%s' % list1[file_label]," with confidence: " '%.4f' %confidence_matrix[file_label])
    for i in range(len(labels)):
            val.append(str(list1[labels[i]].replace("_", " "))+" with confidence: "+str(confidence_matrix[labels[i]]))
    np.savetxt('val_cnn.txt',val,fmt='%s')
    
    """return text file name""" 
    return json.dumps('val_cnn.txt') 

app = Flask(__name__)

@app.route('/api/upload', method=['POST'])
def upload():
    audio = request.files['audio']
    names = birdsname(audio)
    return Response(response = names, status=200, mimetype="application/json")

#start server
app.run(host="10.8.1.10")



    
    
    