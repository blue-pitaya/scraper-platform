package example.models

import io.lemonlabs.uri.Url
import net.ruippeixotog.scalascraper.model.Document

final case class ScrapConfig[A](
    startUrl: Url,
    maxDepth: Int,
    linkFilter: String => Boolean,
    documentParser: (UrlTicket, Document) => Option[A],
    dataSaver: A => Unit
)
