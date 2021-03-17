package common.slick

import akka.NotUsed
import akka.stream.scaladsl.Source
import slick.basic.DatabasePublisher

import scala.concurrent.{ExecutionContext, Future}

abstract class EntityActions extends EntityActionsLike {
  self: JdbcProfileProvider with DatabaseProvider =>

  import profile.api._
  import DBIOExtensions._

  type EntityTable <: Table[Entity] with EntityTableLike

  def tableQuery: TableQuery[EntityTable]

  def idLens: Lens[Entity, Int]

  def fetchAll(fetchSize: Int = 0): Future[Seq[Entity]] =
    db.run {
      tableQuery.result.transactionally.withStatementParameters(fetchSize = fetchSize)
    }

  def getById(id: Int): Future[Entity] =
    db.run {
      filterById(id).result.head
    }

  def findById(id: Int): Future[Option[Entity]] =
    db.run {
      filterById(id).result.headOption
    }

  def insert(entity: Entity)(implicit ec: ExecutionContext): Future[Entity] =
    db.run {
      (tableQuery returning tableQuery.map(_.id) += entity).map(id => idLens.set(entity, id))
    }

  def update(entity: Entity)(implicit ec: ExecutionContext): Future[Entity] =
    db.run {
      for {
        id            <- extractId(entity)
        updatedEntity <- filterById(id).update(entity).mustAffectAtMostOneRow.asTry.map(_ => entity)
      } yield updatedEntity
    }

  def delete(entity: Entity)(implicit ec: ExecutionContext): Future[Int]    =
    db.run {
      extractId(entity).flatMap { id =>
        filterById(id).delete.mustAffectAtMostOneRow
      }
    }

  def deleteById(id: Int)(implicit ec: ExecutionContext): Future[Int] =
    db.run {
      filterById(id).delete.mustAffectAtMostOneRow
    }

  protected def filterById(id: Int): Query[EntityTable, Entity, Seq]

  private def extractId(entity: Entity): DBIO[Int] = DBIO.successful(entity.id.get)
}
