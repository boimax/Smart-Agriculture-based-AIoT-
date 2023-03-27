import threading
import time

from MediatorInterface import BaseComponent, Mediator
from simple_ai import PlantHealthDetection


class BackgroundSensorReader(BaseComponent):

    def __init__(self, mediator: Mediator = None,period: float=3) -> None:
        super().__init__(mediator)
        print("making sensors reader")
        self.event = threading.Event()
        # args must be iterable
        self.background_task = threading.Thread(target=self.send_sensor_data_periodically,args=(period,))
        self.background_task.daemon = True
    def send_sensor_data_periodically(self,period):
        while True:
            t0 = time.time()
            self.mediator.notify(self,"send")
            time.sleep(period-(time.time()-t0))
    def start(self):
        self.background_task.start()

class MoistureReader(BackgroundSensorReader):
    pass
class TempReader(BackgroundSensorReader):
    pass
# Load the model
class PlantHealthReader(BackgroundSensorReader):
    def __init__(self, mediator: Mediator = None,period:float=300) -> None:
        self.detector = PlantHealthDetection()
        super().__init__(mediator,period=period)
    def send_sensor_data_periodically(self,period):
        print("start plant health reader")
        while True:
            t0 = time.time()
            label,image = self.detector.get_plant_condition() #result can be healthy/not healthy, include image?
            # print("hmm",label,image)
            self.mediator.notify(self,(label,image))
            time.sleep(period-(time.time()-t0))