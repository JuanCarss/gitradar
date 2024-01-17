from abc import ABC, abstractmethod


class EventDeserializer(ABC):
    @abstractmethod
    def deserialize(self, event: str):
        pass
