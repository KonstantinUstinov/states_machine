package com.states.machine.utilities

import com.typesafe.config.ConfigFactory

trait ConfigProvider {
  lazy val config = ConfigFactory.load()
}