package io.tintoy.ebt

import akka.actor.ActorRef
import akka.event.{SubchannelClassification, EventBus}
import akka.util.Subclassification

/**
 * An event bus that supports hierarchical topics (e.g. subscriber to topic `/a` will get events for topic `/a` and `/a/b`).
 */
class HierarchicalEventBus extends EventBus with SubchannelClassification {
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
   * Determine the sub-classification for
   * @return
   */
  override protected implicit def subclassification: Subclassification[Classifier] = HierarchicalTopicClassifier

  /**
   * Routing classifier for hierarchical topics (e.g "a/b/c").
   */
  object HierarchicalTopicClassifier extends Subclassification[Classifier] {
    /**
     * Determine whether the specified classifiers are equal.
     * @param topic1 The first topic.
     * @param topic2 The second topic.
     * @return `true` if the topics are the same; otherwise, `false`.
     */
    override def isEqual(topic1: String, topic2: String): Boolean = topic1 == topic2

    /**
     *
     * @param topic1 The first topic.
     * @param topic2 The second topic.
     * @return `true` if the first topic is a sub-topic of the second topic.
     */
    override def isSubclass(topic1: String, topic2: String): Boolean = topic1.startsWith(topic2)
  }
}
