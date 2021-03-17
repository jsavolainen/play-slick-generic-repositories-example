package common.slick

import scala.concurrent.{ExecutionContext, Future}

/**
 * Define basic life cycle actions for a Entity that involve.
 */
trait EntityActionsLike { self: JdbcProfileProvider =>

  /** The type of the Entity */
  type Entity <: EntityLike

  /**
   * Fetch all elements from a table.
   *
   * @param fetchSize The fetch size for all statements or 0 for the default
   * @return StreamingDBIO[Seq[Entity], Entity]
   */
  def fetchAll(fetchSize: Int = 0): Future[Seq[Entity]]

  /**
   * Finds `Entity` referenced by `Id`.
   * May fail if no `Entity` is found for passed `Id`
   *
   * @return DBIO[Entity] for the `Entity`
   */
  def getById(id: Int): Future[Entity]

  /**
   * Finds `Entity` referenced by `Id` optionally.
   *
   * @return DBIO[Option[Entity]] for the `Entity`
   */
  def findById(id: Int): Future[Option[Entity]]

  /**
   * Insert a new `Entity`
   *
   * @return DBIO[Id] for the generated `Id`
   */
  def insert(entity: Entity)(implicit ec: ExecutionContext): Future[Entity]

  /**
   * Update a `Entity`.
   *
   * @return DBIO[Entity] for a `Entity` as persisted in the table.
   */
  def update(entity: Entity)(implicit ec: ExecutionContext): Future[Entity]

  /**
   * Delete a `Entity`.
   *
   * @return DBIO[Int] with the number of affected rows
   */
  def delete(entity: Entity)(implicit ec: ExecutionContext): Future[Int]

  /**
   * Delete a `Entity` by `Id`
   *
   * @return DBIO[Int] with the number of affected rows
   */
  def deleteById(id: Int)(implicit ec: ExecutionContext): Future[Int]
}
