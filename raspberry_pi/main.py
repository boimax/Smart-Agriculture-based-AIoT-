import base64
from AdafruitClient import AdafruitClient
from MediatorInterface import Mediator
from physical import ModbusMaster

import time

from sensor_reader import MoistureReader, PlantHealthReader, TempReader

import io
import PIL.Image as Image

class StateController(Mediator):
    def __init__(self,client:AdafruitClient,serial_connector:ModbusMaster,tempReader:TempReader,moistureReader:MoistureReader,plantHealthReader:PlantHealthReader) -> None:
        self.ada_client = client
        self.serial_connector = serial_connector
        self.temp_reader = tempReader
        self.moisture_reader = moistureReader
        self.plant_health_reader = plantHealthReader

        self.ada_client.mediator = self
        self.temp_reader.mediator = self
        self.moisture_reader.mediator = self
        self.plant_health_reader.mediator = self

        self.temp_reader.start()
        self.moisture_reader.start()
        self.plant_health_reader.start()


    def notify(self, sender: object, event_data) -> None:
        if isinstance(sender,AdafruitClient):
            self._handle_server_message_event(event_data)
        elif isinstance(sender,TempReader):
            self._handle_temperature_event(event_data)
        elif isinstance(sender,MoistureReader):
            self._handle_moisture_event(event_data)
        elif isinstance(sender,PlantHealthReader):
            self._handle_plant_health_event(event_data)
        else:
            raise TypeError("this class cannot notify the mediator")



    def _handle_server_message_event(self,event_data):
        feed_id,payload = event_data
        state = True if payload=="1"  else False
        if feed_id == "actuator1":
            self.serial_connector.switch_actuator_1(state)
        elif feed_id == "actuator2":
            self.serial_connector.switch_actuator_2(state)
        else:
            print("TODO!")
    def _handle_temperature_event(self,event_data):
        self.ada_client.client.publish("sensor0",self.serial_connector.readTemperature()/10)
    def _handle_moisture_event(self,event_data):
        self.ada_client.client.publish("sensor1",self.serial_connector.readMoisture()/10)
    def _handle_plant_health_event(self,event_data):
        label,image = event_data

        # convert image from numpy array to compressed stream
        optim_stream = io.BytesIO()
        Image.fromarray(image).save(optim_stream,format="JPEG",optimize=True,quality=60)
        optim_stream.seek(0)

        value = base64.b64encode(optim_stream.read())
        self.ada_client.client.publish("plant-health",label)
        self.ada_client.client.publish("plant-image",value)

        

#task:
# send temp data every 3 sec client.publish
# send humid data every 5 sec


# wait on input from ada.io to activate pump controller   (how to add desired behaviour in on_message func)
# check image to see if plant is rotten or not, send status to ada.io

AIO_USERNAME="thaotran"
AIO_KEY="aio_bYia38QrXpYz5klG3ZLrnj7p0tqb"

if __name__ == '__main__':

    #component for connecting edge device to adafruit.io
    modbus_master = ModbusMaster()
    adafruit_client = AdafruitClient(AIO_USERNAME,AIO_KEY,["sensor0","sensor1","actuator1","actuator2"])

    #sensors
    tempReader = TempReader(period=4)
    moistureReader = MoistureReader(period=4)
    plantHealthDetector = PlantHealthReader(period=300)


    stateController = StateController(adafruit_client,modbus_master,tempReader,moistureReader,plantHealthDetector)
    
    # cant think of a better way to stop the main thread
    while True:
        time.sleep(100)
    # the main loop will just wait for the child threads to exit
