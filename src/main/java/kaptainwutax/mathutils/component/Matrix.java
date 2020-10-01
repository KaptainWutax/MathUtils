package kaptainwutax.mathutils.component;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.decomposition.LUDecomposition;

public class Matrix {

    private final Rational[][] elements;

    protected Matrix(int rows, int columns) {
        this.elements = new Rational[rows][columns];
    }

    public Matrix(int size, Generator generator) {
        this(size, size, generator);
    }

    public Matrix(int rows, int columns, Generator generator) {
        this(rows, columns);

        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                this.elements[row][column] = generator.getValue(row, column);
            }
        }
    }

    public Matrix(Vector... rows) {
        this(rows.length, rows[0].getDimension(), (row, column) -> rows[row].get(column));
    }

    public Matrix(Rational[]... elements) {
        this(elements.length, elements[0].length, (row, column) -> elements[row][column]);
    }

    public static Matrix zero(int rows, int columns) {
        return new Matrix(rows, columns, (row, column) -> Rational.ZERO);
    }

    public static Matrix identity(int size) {
        return new Matrix(size, size, (row, column) -> row == column ? Rational.ONE : Rational.ZERO);
    }

    public int getRowCount() {
        return this.elements.length;
    }

    public int getColumnCount() {
        return this.elements[0].length;
    }

    public boolean isSquare() {
        return this.getRowCount() == this.getColumnCount();
    }

    public Generator toGenerator() {
        return this::get;
    }

    public Mapper toMapper() {
        return this.toGenerator().asMapper();
    }

    public Rational get(int row, int column) {
        return this.elements[row][column];
    }

    public Matrix set(int row, int column, Rational value) {
        this.elements[row][column] = value;
        return this;
    }

    public Matrix with(int row, int column, Rational value) {
        return this.copy().set(row, column, value);
    }

    public Matrix map(Mapper mapper) {
        return new Matrix(this.getRowCount(), this.getColumnCount(), (row, column) -> mapper.getNewValue(row, column, this.get(row, column)));
    }

    public Matrix mapAndSet(Mapper mapper) {
        for(int row = 0; row < this.getRowCount(); row++) {
            for(int column = 0; column < this.getColumnCount(); column++) {
                this.set(row, column, mapper.getNewValue(row, column, this.get(row, column)));
            }
        }

        return this;
   }

   public Matrix mapRow(int row, Vector.Mapper mapper) {
       return new Matrix(this.getRowCount(), this.getColumnCount(), (row1, column) -> row == row1 ? mapper.getNewValue(column, this.get(row, column)) : this.get(row1, column));
   }

   public Matrix mapRowAndSet(int row, Vector.Mapper mapper) {
       for(int column = 0; column < this.getColumnCount(); column++) {
           this.set(row, column, mapper.getNewValue(column, this.get(row, column)));
       }

       return this;
   }

    public Matrix mapColumn(int column, Vector.Mapper mapper) {
        return new Matrix(this.getRowCount(), this.getColumnCount(), (row, column1) -> column == column1 ? mapper.getNewValue(row, this.get(row, column)) : this.get(row, column1));
    }

    public Matrix mapColumnAndSet(int column, Vector.Mapper mapper) {
        for(int row = 0; row < this.getRowCount(); row++) {
            this.set(row, column, mapper.getNewValue(row, this.get(row, column)));
        }

        return this;
    }

    public Vector.View getRow(int row) {
        return new Vector.View(this.getColumnCount(),
                column -> this.get(row, column), (column, value) -> this.set(row, column, value));
    }

    public Vector.View getColumn(int column) {
        return new Vector.View(this.getRowCount(),
                row -> this.get(row, column), (row, value) -> this.set(row, column, value));
    }

    public Vector getRowCopy(int row) {
        return new Vector(this.getColumnCount(), i -> this.get(row, i));
    }

    public Vector getColumnCopy(int column) {
        return new Vector(this.getRowCount(), i -> this.get(i, column));
    }

    public Matrix setRow(int row, Vector value) {
        return this.mapRowAndSet(row, (index, oldValue) -> value.get(index));
    }

    public Matrix setColumn(int column, Vector value) {
        return this.mapColumnAndSet(column, (index, oldValue) -> value.get(index));
    }

    public Matrix withRow(int row, Vector value) {
        return this.mapRow(row, value.toMapper());
    }

    public Matrix withColumn(int column, Vector value) {
        return this.mapColumn(column, value.toMapper());
    }

    public Vector.View[] getRows() {
        Vector.View[] rows = new Vector.View[this.getRowCount()];
        for(int i = 0; i < rows.length; i++)rows[i] = this.getRow(i);
        return rows;
    }

    public Vector.View[] getColumns() {
        Vector.View[] columns = new Vector.View[this.getColumnCount()];
        for(int i = 0; i < columns.length; i++)columns[i] = this.getColumn(i);
        return columns;
    }

    public Vector[] getRowsCopy() {
        Vector[] rows = new Vector[this.getRowCount()];
        for(int i = 0; i < rows.length; i++)rows[i] = this.getRowCopy(i);
        return rows;
    }

    public Vector[] getColumnsCopy() {
        Vector[] columns = new Vector[this.getColumnCount()];
        for(int i = 0; i < columns.length; i++)columns[i] = this.getColumnCopy(i);
        return columns;
    }

    public Matrix swap(int r1, int c1, int r2, int c2) {
        return this.map((row, column, oldValue) -> {
            if(row == r1 && column == c1) {
                row = r2; column = c2;
            } else if(row == r2 && column == c2) {
                row = r1; column = c1;
            }

            return this.get(row, column);
        });
    }

    public Matrix swapRows(int r1, int r2) {
        return this.map((row, column, oldValue) -> {
            if(row == r1)row = r2;
            else if(row == r2)row = r1;
            return this.get(row, column);
        });
    }

    public Matrix swapColumns(int c1, int c2) {
        return this.map((row, column, oldValue) -> {
            if(column == c1)column = c2;
            else if(column == c2)column = c1;
            return this.get(row, column);
        });
    }

    public Matrix swapAndSet(int r1, int c1, int r2, int c2) {
        Rational oldValue = this.get(r1, c1);
        return this.set(r1, c1, this.get(r2, c2)).set(r2, c2, oldValue);
    }

    public Matrix swapRowsAndSet(int r1, int r2) {
        Vector oldRow = this.getRowCopy(r1);
        return this.mapRowAndSet(r1, (index, oldValue) -> this.get(r2, index)).mapRowAndSet(r2, oldRow.toMapper());
    }

    public Matrix swapColumnsAndSet(int c1, int c2) {
        Vector oldColumn = this.getColumnCopy(c1);
        return this.mapColumnAndSet(c1, (index, oldValue) -> this.get(c2, index)).mapColumnAndSet(c2, oldColumn.toMapper());
    }

    public Matrix transpose() {
        return new Matrix(this.getColumnCount(), this.getRowCount(), (row, column) -> this.get(column, row));
    }

    public Matrix transposeAndSet() {
        if(!this.isSquare()) {
            throw new IllegalStateException("Mutating a non-square matrix");
        }

        return this.mapAndSet((row, column, oldValue) -> this.get(column, row));
    }

    public Matrix add(Matrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.map((row, column, oldValue) -> oldValue.add(other.get(row, column)));
    }

    public Matrix addAndSet(Matrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.mapAndSet((row, column, oldValue) -> oldValue.add(other.get(row, column)));
    }

    public Matrix subtract(Matrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.map((row, column, oldValue) -> oldValue.subtract(other.get(row, column)));
    }

    public Matrix subtractAndSet(Matrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.mapAndSet((row, column, oldValue) -> oldValue.subtract(other.get(row, column)));
    }

    public Matrix multiply(Matrix other) {
        if(this.getColumnCount() != other.getRowCount()) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        Vector[] rows = this.getRows();
        Vector[] columns = other.getColumns();
        return this.map((row, column, oldValue) -> rows[row].dot(columns[column]));
    }

    public Matrix multiplyAndSet(Matrix other) {
        if(this.getColumnCount() != other.getRowCount()) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        Vector[] rows = this.getRows();
        Vector[] columns = other.getColumns();
        return this.mapAndSet((row, column, oldValue) -> rows[row].dot(columns[column]));
    }

    public Vector multiply(Vector vector) {
        return vector.multiply(this);
    }

    public Vector multiplyAndSet(Vector vector) {
        return vector.multiplyAndSet(this);
    }

    public Matrix multiply(Rational scalar) {
        return this.map((row, column, oldValue) -> this.get(row, column).multiply(scalar));
    }

    public Matrix multiplyAndSet(Rational scalar) {
        return this.mapAndSet((row, column, oldValue) -> this.get(row, column).multiply(scalar));
    }

    public Matrix divide(Rational scalar) {
        return this.map((row, column, oldValue) -> this.get(row, column).divide(scalar));
    }

    public Matrix divideAndSet(Rational scalar) {
        return this.mapAndSet((row, column, oldValue) -> this.get(row, column).divide(scalar));
    }

    public Matrix invert() {
        return this.luDecompose().getInverse();
    }

    public Matrix invertAndSet() {
        Matrix inverse = this.invert();
        return this.mapAndSet((row, column, oldValue) -> inverse.get(row, column));
    }

    public Rational getDeterminant() {
        return this.luDecompose().getDeterminant();
    }

    public LUDecomposition luDecompose() {
        return new LUDecomposition(this);
    }

    public Matrix sub(int r1, int c1, int rowCount, int columnCount) {
        return new Matrix.View(rowCount, columnCount,
                (row, column) -> this.get(r1 + row, c1 + column),
                (row, column, value) -> this.set(r1 + row, c1 + column, value));
    }

    public Matrix subCopy(int r1, int c1, int rowCount, int columnCount) {
        return this.sub(r1, c1, rowCount, columnCount).copy();
    }

    public Matrix.Augmented mergeToAugmented(Matrix extra) {
        if(this.getRowCount() != extra.getRowCount()) {
            throw new UnsupportedOperationException("Merging two matrices with different row count");
        }

        return new Matrix.Augmented(this, extra);
    }

    public Matrix.Augmented splitToAugmented(int columnSplit) {
        return new Matrix.Augmented(this, columnSplit);
    }

    public Matrix copy() {
        return new Matrix(this.getRowCount(), this.getColumnCount(), this.toGenerator());
    }

    @Override
    public int hashCode() {
        int result = 1;

        for(int row = 0; row < this.getRowCount(); row++) {
            result = 31 * result + this.getRow(row).hashCode();
        }

        return this.getRowCount() * 961 + this.getColumnCount() * 31 + result;
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof Matrix))return false;
        Matrix matrix = (Matrix)other;
        if(this.getRowCount() != matrix.getRowCount())return false;
        if(this.getColumnCount() != matrix.getColumnCount())return false;

        for(int row = 0; row < this.getRowCount(); row++) {
            for(int column = 0; column < this.getColumnCount(); column++) {
                if(!this.get(row, column).equals(matrix.get(row, column)))return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Vector[] rows = this.getRows();

        for(int i = 0; i < rows.length; i++) {
            sb.append(rows[i].toString()).append(i < rows.length - 1 ? "\n" : "");
        }

        return sb.toString();
    }

    public static class View extends Matrix {
        private final int rows;
        private final int columns;
        private final Generator getter;
        private final View.Setter setter;

        public View(int rows, int columns, Generator getter, Setter setter) {
            super(0, 0);
            this.rows = rows;
            this.columns = columns;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int getRowCount() {
            return this.rows;
        }

        @Override
        public int getColumnCount() {
            return this.columns;
        }

        @Override
        public Rational get(int row, int column) {
            return this.getter.getValue(row, column);
        }

        @Override
        public Matrix set(int row, int column, Rational value) {
            this.setter.set(row, column, value);
            return this;
        }

        @FunctionalInterface
        public interface Setter {
            void set(int row, int column, Rational value);
        }
    }

    public static class Augmented extends Matrix {
        private final Matrix base;
        private final Matrix extra;
        private final int split;

        public Augmented(Matrix base, Matrix extra) {
            super(0, 0);
            this.base = base;
            this.extra = extra;
            this.split = base.getColumnCount();
        }

        public Augmented(Matrix merged, int split) {
            this(merged.sub(0, 0, merged.getRowCount() - 1, split - 1),
                    merged.sub(0, 0, merged.getRowCount() - 1, split - 1));
        }

        public Matrix getBaseMatrix() {
            return this.base;
        }

        public Matrix getExtraMatrix() {
            return this.extra;
        }

        public int getSplit() {
            return this.split;
        }

        @Override
        public int getRowCount() {
            return this.getBaseMatrix().getRowCount();
        }

        @Override
        public int getColumnCount() {
            return this.getBaseMatrix().getColumnCount() + this.getExtraMatrix().getColumnCount();
        }

        @Override
        public Rational get(int row, int column) {
            return column < this.getSplit() ? this.getBaseMatrix().get(row, column)
                    : this.getExtraMatrix().get(row, column - this.getSplit());
        }

        @Override
        public Matrix set(int row, int column, Rational value) {
            if(column < this.getSplit()) {
                this.getBaseMatrix().set(row, column, value);
            } else {
                this.getExtraMatrix().set(row, column - this.getSplit(), value);
            }

            return this;
        }
    }

    @FunctionalInterface
    public interface Generator {
        Rational getValue(int row, int column);

        default Vector.Generator forRow(int row) {
            return index -> this.getValue(row, index);
        }

        default Vector.Generator forColumn(int column) {
            return index -> this.getValue(index, column);
        }

        default Mapper asMapper() {
            return (row, column, oldValue) -> this.getValue(row, column);
        }
    }

    @FunctionalInterface
    public interface Mapper {
        Rational getNewValue(int row, int column, Rational oldValue);

        default Vector.Mapper forRow(int row) {
            return (index, oldValue) -> this.getNewValue(row, index, oldValue);
        }

        default Vector.Mapper forColumn(int column) {
            return (index, oldValue) -> this.getNewValue(index, column, oldValue);
        }

        default Generator asGenerator() {
            return (row, column) -> this.getNewValue(row, column, null);
        }
    }

}
