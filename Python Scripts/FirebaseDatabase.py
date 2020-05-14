from firebase import firebase
firebase = firebase.FirebaseApplication('https://takepicture-8a86e.firebaseio.com/', None)
data = {
        "Name":"Piyush Goyal 2",
        'Age':192
        }
result = firebase.post('/takepicture-8a86e/', data)
result = firebase.get('/takepicture-8a86e/','')
print(result)