import sys
from Adafruit_IO import MQTTClient
from MediatorInterface import BaseComponent

class AdafruitClient(BaseComponent):
    def __init__(self,username:str,key: str,subscribe_topics: list[str]):
        super().__init__()
        client = MQTTClient(username, key)
        def connected(client):
            print("Ket noi thanh cong...")
            for topic in subscribe_topics:
                client.subscribe(topic)

        def subscribe(client, userdata, mid, granted_qos):
            print("Subscribe thanh cong...",userdata,mid,granted_qos,client)

        def disconnected(client):
            print("Ngat ket noi...")
            sys.exit(1)
        def message(client, feed_id, payload):
            # print("Nhan du lieu " + feed_id + ":" + payload)
            self.mediator.notify(self,(feed_id,payload))
    
        client.on_connect = connected
        client.on_disconnect = disconnected
        client.on_subscribe = subscribe
        client.on_message = message 
        client.connect()
        client.loop_background()
        self.client = client