package io.tintoy.ebt

/**
 * The container for events on a [[SimpleEventBus]].
 * @param topic The event topic.
 * @param message The event payload.
 */
case class Envelope(topic: String, message: String)
