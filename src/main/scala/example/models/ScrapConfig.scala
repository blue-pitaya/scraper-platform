package example.models

import io.lemonlabs.uri.Url
import net.ruippeixotog.scalascraper.model.Document
import example.savers.DataSaverConfig

final case class ScrapConfig[A](
    startUrl: Url,
    maxDepth: Int,
    linkFilter: String => Boolean,
    documentParser: (UrlTicket, Document) => Option[A],
    dataSaver: DataSaverConfig[A]
)
