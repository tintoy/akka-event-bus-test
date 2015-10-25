package io.tintoy.ebt

import akka.actor.ActorRef
import akka.event.{LookupClassification, EventBus}

/**
 * A simple event bus that sends notifications to subscribers associated with a topic.
 */
class SimpleEventBus extends EventBus with LookupClassification {
  /**
   * The containing type for events on the bus.
   */
  override type Event = Envelope

  /**
   * The type used to route events on the bus.
   */
  override type Classifier = String

  /**
   * The type that subscribes to events on the bus.
   */
  override type Subscriber = ActorRef

  /**
   * The initial size of the data structure used for lookups.
   */
  override protected def mapSize(): Int = 128

  /**
   * Publish an event to a subscriber.
   * @param event The [[Envelope]] representing the event to publish.
   * @param subscriber The subscriber to which the event will be published.
   */
  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.message
  }

  /**
   * Determine the routing classifier for an event.
   * @param event The event.
   * @return The event classier ([[Envelope.topic]]).
   */
  override protected def classify(event: Event): Classifier = event.topic

  /**
   * Compare 2 subscribers.
   * @param subscriber1 The first subscriber.
   * @param subscriber2 The first subscriber.
   * @return The comparison result (0 if they are equivalent).
   * @note Required for insertion into an ordered map.
   */
  override protected def compareSubscribers(subscriber1: Subscriber, subscriber2: Subscriber): Int = {
    subscriber1.compareTo(subscriber2)
  }
}

