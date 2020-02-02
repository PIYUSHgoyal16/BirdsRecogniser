import cv2
import numpy as np

img = cv2.imread([file_path], cv2.IMREAD_GRAYSCALE)

laplacian = cv2.Laplacian(img, cv2.CV_64F)
laplacian_var = cv2.Laplacian(img, cv2.CV_64F).var()

cv2.imshow("Img",img)
cv2.imshow("Laplacian", laplacian)

#bluriness can be decided using the value of laplacian_var
print(laplacian_var)

cv2.waitkey(0)
cv2.destroyAllWindows()
