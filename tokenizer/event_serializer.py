from abc import ABC, abstractmethod


class EventSerializer(ABC):
    @abstractmethod
    def serialize(self, event):
        pass
