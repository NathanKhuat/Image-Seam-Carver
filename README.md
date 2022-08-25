# Image-Seam-Carver
My Image Seam Carver was the final project for my **Sophomore Data Structures and Algorithms** course. In this project, I applied **Dijkstra's algorithm** along 
with **Dynamic Programming** to create an application that preserves scales within images when cropped.

Our algorithm converts the pixels of a given image into a weighted directed graph with color values. We utilize shortest path algorithms to find paths of pixels 
with the **least difference in color** to crop the image while preserving the main components within the picture. In practice, we found that landscape photos work
the best as relative color values are fairly small. 

## **Example:** 
We start with a landscape images as shown.

![Ocean Landscape](https://user-images.githubusercontent.com/86175076/186546846-a22c69e8-cd29-4880-997d-b1b1f0287134.png)

Applying the algorithm to crop the image, we are given the image below. Notice that the surfers in the foreground and peninsula in the distance are the same. 

![result](https://user-images.githubusercontent.com/86175076/186547305-36add6e4-e6c5-4e36-96ae-e2c12285c24a.png)

