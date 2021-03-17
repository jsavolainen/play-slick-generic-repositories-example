package common.slick

import slick.lifted.Rep

trait EntityTableLike {
  def id: Rep[Int]
}
