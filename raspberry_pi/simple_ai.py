print("Simple AI")
import threading
from PIL import Image, ImageOps
import numpy as np
import cv2
import torch
import clip

class PlantHealthDetection():
    def __init__(self) -> None:

        # start a thread to continuously read and discard the buffer
        self.cap = cv2.VideoCapture(0)
        self.remove_buffer_thread = threading.Thread(target=self._grab_frame)
        self.remove_buffer_thread.daemon = True
        self.remove_buffer_thread.start()

        
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model, self.preprocess = clip.load("ViT-L/14", device=self.device)
        self.labels = ["healthy plant", "diseased plant","withered plant","other"]
        self.tokenized_labels = clip.tokenize(["healthy plant", "diseased plant","withered plant","other"]).to(self.device)
    
    def get_plant_condition(self):
        with torch.no_grad():
            _,frame = self.cap.retrieve()
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            image = self.preprocess(Image.fromarray(frame,mode="RGB")).unsqueeze(0).to(self.device)
            logits_per_image, logits_per_text = self.model(image, self.tokenized_labels)
            probs = logits_per_image.softmax(dim=-1).cpu().numpy()
            ind = np.argmax(probs)
            return self.labels[ind],frame
    def _grab_frame(self):
        while True:
            ret = self.cap.grab()
            if not ret:
                break


