from abc import ABC


class Mediator(ABC):
    """
    The Mediator interface declares a method used by components to notify the
    mediator about various events. The Mediator may react to these events and
    pass the execution to other components.
    """
    def notify(self, sender: object, event) -> None:
        pass


class BaseComponent:
    def __init__(self,mediator:Mediator=None) -> None:
        self._mediator = mediator
    
    @property
    def mediator(self)->Mediator:
        return self._mediator

    @mediator.setter
    def mediator(self, mediator: Mediator) -> None:
        self._mediator = mediator
