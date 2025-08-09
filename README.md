# Potato Leaf Disease Classification

An end-to-end machine learning and mobile application project for detecting potato leaf diseases (such as Late Blight and Early Blight) from images.  
The solution combines a TensorFlow deep learning model, deployed on Google Cloud Functions, with an Android mobile app built in Java.

---

## 📌 Features

- **Image Classification** – Predicts potato leaf diseases from photos taken via camera or selected from the gallery.
- **Cloud-Based Inference** – TensorFlow/Keras model hosted on Google Cloud Functions for scalable, serverless predictions.
- **Real-Time Response** – Sends images via `multipart/form-data` POST requests and receives JSON responses with class name & confidence score.
- **Offline-First App** – Android Java app with a clean UI for selecting, previewing, and submitting images.
- **Model Management** – Model stored in Google Cloud Storage and loaded dynamically when Cloud Function starts.

---

## 🛠️ Tech Stack

### **Mobile App**
- **Language**: Java (Android)
- **Networking**: `HttpURLConnection` for sending multipart image requests
- **UI**: `ImageView`, `TextView`, camera/gallery picker

### **Machine Learning**
- **Framework**: TensorFlow / Keras
- **Model Type**: Convolutional Neural Network (CNN)
- **Input Shape**: 256×256 RGB images
- **Preprocessing**: Resizing, normalization (0–1 range)

### **Cloud Deployment**
- **Platform**: Google Cloud Functions (Gen1)
- **Storage**: Google Cloud Storage for model hosting
- **Runtime**: Python 3.8 (TensorFlow compatible)

---

## 🚀 How It Works

1. **Model Training**  
   - Train CNN model on potato leaf dataset (e.g., PlantVillage dataset).
   - Save model as `.h5` and upload to Google Cloud Storage.

2. **Backend (Cloud Function)**  
   - Loads the model from Cloud Storage on first invocation.
   - Accepts HTTP POST requests with an image file.
   - Preprocesses the image and runs model inference.
   - Returns JSON:  
     ```json
     {
       "class": "Late Blight",
       "confidence": 97.53
     }
     ```

3. **Android App**  
   - User selects or captures an image.
   - App sends the image to the Cloud Function endpoint.
   - Displays predicted class and confidence score.
   - Open mobile-app/ in Android Studio.
   - Update CLOUD_FUNCTION_URL in MainActivity.java with your deployed endpoint. Build and run on device/emulator.
---
## 📊 Example API Response
- ***Request (POST)***:
  ``` file: <image.jpg> ```
- ***Response (GET)***:
  ```json
  {
  "class": "Late Blight",
  "confidence": 97.53
  }
  ```
---
## 🧠 Model Info
Architecture: Custom CNN with Conv2D, MaxPooling, Dense layers.

Dataset: PlantVillage potato subset.

Training Accuracy: ~XX%

Validation Accuracy: ~XX%

---

