package common.slick

import common.slick.exceptions._
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

object DBIOExtensions {
  implicit class UpdateActionExtensionMethods(dbAction: DBIO[Int])       {
    def mustAffectAtMostOneRow(implicit ec: ExecutionContext): DBIO[Int]  = {
      dbAction.flatMap {
        case 1          => DBIO.successful(1)
        case 0          => DBIO.successful(0)
        case n if n > 1 => DBIO.failed(new TooManyRowsAffectedException(affectedRowCount = n, expectedRowCount = 1))
      }
    }

    def mustAffectAtLeastOneRow(implicit ec: ExecutionContext): DBIO[Int] = {
      dbAction.flatMap {
        case n if n >= 1 => DBIO.successful(n) // expecting one or more results
        case _           => DBIO.failed(NoRowsAffectedException)
      }
    }
  }

  implicit class SelectSingleExtensionMethods[R](dbAction: DBIO[Seq[R]]) {
    def mustSelectSingleRow(implicit ec: ExecutionContext): DBIO[R] = {
      dbAction.flatMap {
        case s if s.size == 1 => DBIO.successful(s.head)
        case s if s.isEmpty   => DBIO.failed(RowNotFoundException)
        case s                => DBIO.failed(new TooManyRowsAffectedException(affectedRowCount = s.size, expectedRowCount = 1))
      }
    }
  }

}
