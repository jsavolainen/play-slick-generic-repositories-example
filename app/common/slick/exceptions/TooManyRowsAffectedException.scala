package common.slick.exceptions

class TooManyRowsAffectedException(affectedRowCount: Int, expectedRowCount: Int)
    extends RepositoryException(s"Expected $expectedRowCount row(s) affected, got $affectedRowCount instead")
