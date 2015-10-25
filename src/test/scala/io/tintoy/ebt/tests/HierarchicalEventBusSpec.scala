package io.tintoy.ebt.tests

import akka.actor.{Actor, ActorSystem}
import akka.testkit._
import com.typesafe.config.ConfigFactory
import io.tintoy.ebt.{Envelope, HierarchicalEventBus}
import org.scalatest.{BeforeAndAfterEach, WordSpecLike, Matchers, BeforeAndAfterAll}
import scala.concurrent.duration.DurationInt

/**
 * Tests for [[HierarchicalEventBus]].
 */
class HierarchicalEventBusSpec
  extends TestKit(
    ActorSystem("HierarchicalEventBusSpec",
      ConfigFactory.defaultApplication()
    )
  ) with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers
  with BeforeAndAfterAll with BeforeAndAfterEach {

  var eventBus: HierarchicalEventBus = null

  "Hierarchical event bus with 1 subscriber for 1 topic" should {
    "send events to that subscriber if they are published with that topic" in {
      within(500.milliseconds) {
        eventBus.subscribe(self, to = "/topic1")
        eventBus.publish(
          Envelope(
            topic = "/topic1",
            message = "Test"
          )
        )
        expectMsg("Test")

        eventBus.unsubscribe(self)
      }
    }

    "not send events to that subscriber if they are published with a different topic" in {
      within(500.milliseconds) {
        eventBus.subscribe(self, to = "/topic1")
        eventBus.publish(
          Envelope(
            topic = "/topic1/sub-topic",
            message = "Test"
          )
        )
        expectMsg("Test")

        eventBus.unsubscribe(self)
      }
    }

    "not send events to that subscriber for a different topic" in {
      within(500.milliseconds) {
        eventBus.subscribe(self, to = "/topic1")
        eventBus.publish(
          Envelope(
            topic = "/topic2",
            message = "Test"
          )
        )
        expectNoMsg()

        eventBus.unsubscribe(self)
      }
    }
  }

  "Hierarchical event bus with 2 subscriber for 2 different topics" should {
    "send events only to the subscriber whose topic is in or under the one the event was published with" in {
      within(500.milliseconds) {
        val topic1Subscriber = TestProbe("topic-1-subscriber")
        val topic2Subscriber = TestProbe("topic-2-subscriber")

        eventBus.subscribe(topic1Subscriber.ref, to = "/topic1")
        eventBus.subscribe(topic2Subscriber.ref, to = "/topic2")
        eventBus.publish(
          Envelope(
            topic = "/topic1",
            message = "Test"
          )
        )
        topic1Subscriber.expectMsg("Test")
        topic2Subscriber.expectNoMsg(max = 100.milliseconds)

        eventBus.unsubscribe(topic1Subscriber.ref)
        eventBus.unsubscribe(topic2Subscriber.ref)
      }
    }
  }

  /**
   * Setup before any tests are run.
   */
  override protected def beforeAll(): Unit = super.beforeAll()

  /**
   * Tear-down after all tests are run.
   */
  override protected def afterAll(): Unit = {
    shutdown()

    super.afterAll()
  }

  /**
   * Setup before each test is run.
   */
  override protected def beforeEach(): Unit = {
    super.beforeEach()

    eventBus = new HierarchicalEventBus()
  }

  /**
   * Tear-down after each test is run.
   */
  override protected def afterEach(): Unit = {
    eventBus = null

    super.afterEach()
  }
}
