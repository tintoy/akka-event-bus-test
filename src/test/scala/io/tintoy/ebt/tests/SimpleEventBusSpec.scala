package io.tintoy.ebt.tests

import akka.actor.{ActorSystem}
import akka.testkit._
import com.typesafe.config.ConfigFactory
import io.tintoy.ebt.SimpleEventBus
import io.tintoy.ebt.SimpleEventBus._
import org.scalatest.{BeforeAndAfterEach, WordSpecLike, Matchers, BeforeAndAfterAll}
import scala.concurrent.duration.DurationInt

/**
 * Tests for [[SimpleEventBus]].
 */
class SimpleEventBusSpec
    extends TestKit(
      ActorSystem("SimpleEventBusSpec",
        ConfigFactory.defaultApplication()
      )
    ) with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers
    with BeforeAndAfterAll with BeforeAndAfterEach {

  var eventBus: SimpleEventBus = null

  "Simple event bus with 1 topic and one subscriber" should {
    "send events to a subscriber for that topic" in {
      within(500.milliseconds) {
        eventBus.subscribe(self, to = "topic1")
        eventBus.publish(
          Envelope(
            topic = "topic1",
            message = "Test"
          )
        )
        expectMsg("Test")

        eventBus.unsubscribe(self)
      }
    }

    "not send events to a subscriber for a different topic" in {
      within(500.milliseconds) {
        eventBus.subscribe(self, to = "topic1")
        eventBus.publish(
          Envelope(
            topic = "topic2",
            message = "Test"
          )
        )
        expectNoMsg()

        eventBus.unsubscribe(self)
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

    eventBus = new SimpleEventBus()
  }

  /**
   * Tear-down after each test is run.
   */
  override protected def afterEach(): Unit = {
    eventBus = null

    super.afterEach()
  }
}
