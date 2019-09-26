package com.evolutiongaming.scassandra

import com.datastax.driver.core.SocketOptions
import com.typesafe.config.Config
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

import scala.concurrent.duration._

/**
  * See [[https://docs.datastax.com/en/developer/java-driver/3.5/manual/socket_options/]]
  */
final case class SocketConfig(
  connectTimeout: FiniteDuration = 5.seconds,
  readTimeout: FiniteDuration = 12.seconds,
  keepAlive: Option[Boolean] = None,
  reuseAddress: Option[Boolean] = None,
  soLinger: Option[Int] = None,
  tcpNoDelay: Option[Boolean] = Some(true),
  receiveBufferSize: Option[Int] = None,
  sendBufferSize: Option[Int] = None) {

  def asJava: SocketOptions = {
    val socketOptions = new SocketOptions()
      .setConnectTimeoutMillis(connectTimeout.toMillis.toInt)
      .setReadTimeoutMillis(readTimeout.toMillis.toInt)

    keepAlive.foreach(socketOptions.setKeepAlive)
    reuseAddress.foreach(socketOptions.setReuseAddress)
    soLinger.foreach(socketOptions.setSoLinger)
    tcpNoDelay.foreach(socketOptions.setTcpNoDelay)
    receiveBufferSize.foreach(socketOptions.setReceiveBufferSize)
    sendBufferSize.foreach(socketOptions.setSendBufferSize)

    socketOptions
  }
}

object SocketConfig {

  val Default: SocketConfig = SocketConfig()

  implicit val configReaderSocketConfig: ConfigReader[SocketConfig] = deriveReader


  @deprecated("use ConfigReader instead", "1.1.5")
  def apply(config: Config): SocketConfig = apply(config, Default)

  def apply(config: Config, default: => SocketConfig): SocketConfig = {
    ConfigSource.fromConfig(config).load[SocketConfig] getOrElse default
  }
}
