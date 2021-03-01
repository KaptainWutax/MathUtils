package kaptainwutax.mathutils.component.matrix;

import kaptainwutax.mathutils.arithmetic.Real;
import kaptainwutax.mathutils.component.vector.CVector;
import kaptainwutax.mathutils.component.vector.RVector;
import kaptainwutax.mathutils.decomposition.LUDecomposition;

public class RMatrix {

    private final Real[][] elements;

    protected RMatrix(int rows, int columns) {
        this.elements = new Real[rows][columns];
    }

    public RMatrix(int size, Generator generator) {
        this(size, size, generator);
    }

    public RMatrix(int rows, int columns, Generator generator) {
        this(rows, columns);

        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                this.elements[row][column] = generator.getValue(row, column);
            }
        }
    }

    public RMatrix(RVector... rows) {
        this(rows.length, rows[0].getDimension(), (row, column) -> rows[row].get(column));
    }

    public RMatrix(Real[]... elements) {
        this(elements.length, elements[0].length, (row, column) -> elements[row][column]);
    }

    public static RMatrix zero(int rows, int columns) {
        return new RMatrix(rows, columns, (row, column) -> Real.ZERO);
    }

    public static RMatrix identity(int size) {
        return new RMatrix(size, size, (row, column) -> row == column ? Real.ONE : Real.ZERO);
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

    public Real get(int row, int column) {
        return this.elements[row][column];
    }

    public RMatrix set(int row, int column, Real value) {
        this.elements[row][column] = value;
        return this;
    }

    public RMatrix with(int row, int column, Real value) {
        return this.copy().set(row, column, value);
    }

    public RMatrix map(Mapper mapper) {
        return new RMatrix(this.getRowCount(), this.getColumnCount(), (row, column) -> mapper.getNewValue(row, column, this.get(row, column)));
    }

    public RMatrix mapAndSet(Mapper mapper) {
        for(int row = 0; row < this.getRowCount(); row++) {
            for(int column = 0; column < this.getColumnCount(); column++) {
                this.set(row, column, mapper.getNewValue(row, column, this.get(row, column)));
            }
        }

        return this;
   }

   public RMatrix mapRow(int row, RVector.Mapper mapper) {
       return new RMatrix(this.getRowCount(), this.getColumnCount(), (row1, column) -> row == row1 ? mapper.getNewValue(column, this.get(row, column)) : this.get(row1, column));
   }

   public RMatrix mapRowAndSet(int row, RVector.Mapper mapper) {
       for(int column = 0; column < this.getColumnCount(); column++) {
           this.set(row, column, mapper.getNewValue(column, this.get(row, column)));
       }

       return this;
   }

    public RMatrix mapColumn(int column, RVector.Mapper mapper) {
        return new RMatrix(this.getRowCount(), this.getColumnCount(), (row, column1) -> column == column1 ? mapper.getNewValue(row, this.get(row, column)) : this.get(row, column1));
    }

    public RMatrix mapColumnAndSet(int column, RVector.Mapper mapper) {
        for(int row = 0; row < this.getRowCount(); row++) {
            this.set(row, column, mapper.getNewValue(row, this.get(row, column)));
        }

        return this;
    }

    public RVector.View getRow(int row) {
        return new RVector.View(this.getColumnCount(),
                column -> this.get(row, column), (column, value) -> this.set(row, column, value));
    }

    public RVector.View getColumn(int column) {
        return new RVector.View(this.getRowCount(),
                row -> this.get(row, column), (row, value) -> this.set(row, column, value));
    }

    public RVector getRowCopy(int row) {
        return new RVector(this.getColumnCount(), i -> this.get(row, i));
    }

    public RVector getColumnCopy(int column) {
        return new RVector(this.getRowCount(), i -> this.get(i, column));
    }

    public RMatrix setRow(int row, RVector value) {
        return this.mapRowAndSet(row, (index, oldValue) -> value.get(index));
    }

    public RMatrix setColumn(int column, RVector value) {
        return this.mapColumnAndSet(column, (index, oldValue) -> value.get(index));
    }

    public RMatrix withRow(int row, RVector value) {
        return this.mapRow(row, value.toMapper());
    }

    public RMatrix withColumn(int column, RVector value) {
        return this.mapColumn(column, value.toMapper());
    }

    public RVector.View[] getRows() {
        RVector.View[] rows = new RVector.View[this.getRowCount()];
        for(int i = 0; i < rows.length; i++)rows[i] = this.getRow(i);
        return rows;
    }

    public RVector.View[] getColumns() {
        RVector.View[] columns = new RVector.View[this.getColumnCount()];
        for(int i = 0; i < columns.length; i++)columns[i] = this.getColumn(i);
        return columns;
    }

    public RVector[] getRowsCopy() {
        RVector[] rows = new RVector[this.getRowCount()];
        for(int i = 0; i < rows.length; i++)rows[i] = this.getRowCopy(i);
        return rows;
    }

    public RVector[] getColumnsCopy() {
        RVector[] columns = new RVector[this.getColumnCount()];
        for(int i = 0; i < columns.length; i++)columns[i] = this.getColumnCopy(i);
        return columns;
    }

    public RMatrix swap(int r1, int c1, int r2, int c2) {
        return this.map((row, column, oldValue) -> {
            if(row == r1 && column == c1) {
                row = r2; column = c2;
            } else if(row == r2 && column == c2) {
                row = r1; column = c1;
            }

            return this.get(row, column);
        });
    }

    public RMatrix swapRows(int r1, int r2) {
        return this.map((row, column, oldValue) -> {
            if(row == r1)row = r2;
            else if(row == r2)row = r1;
            return this.get(row, column);
        });
    }

    public RMatrix swapColumns(int c1, int c2) {
        return this.map((row, column, oldValue) -> {
            if(column == c1)column = c2;
            else if(column == c2)column = c1;
            return this.get(row, column);
        });
    }

    public RMatrix swapAndSet(int r1, int c1, int r2, int c2) {
        Real oldValue = this.get(r1, c1);
        return this.set(r1, c1, this.get(r2, c2)).set(r2, c2, oldValue);
    }

    public RMatrix swapRowsAndSet(int r1, int r2) {
        RVector oldRow = this.getRowCopy(r1);
        return this.mapRowAndSet(r1, (index, oldValue) -> this.get(r2, index)).mapRowAndSet(r2, oldRow.toMapper());
    }

    public RMatrix swapColumnsAndSet(int c1, int c2) {
        RVector oldColumn = this.getColumnCopy(c1);
        return this.mapColumnAndSet(c1, (index, oldValue) -> this.get(c2, index)).mapColumnAndSet(c2, oldColumn.toMapper());
    }

    public RMatrix transpose() {
        return new RMatrix(this.getColumnCount(), this.getRowCount(), (row, column) -> this.get(column, row));
    }

    public RMatrix transposeAndSet() {
        if(!this.isSquare()) {
            throw new IllegalStateException("Mutating a non-square matrix");
        }

        return this.mapAndSet((row, column, oldValue) -> this.get(column, row));
    }

    public RMatrix add(RMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.map((row, column, oldValue) -> oldValue.add(other.get(row, column)));
    }

    public RMatrix addAndSet(RMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.mapAndSet((row, column, oldValue) -> oldValue.add(other.get(row, column)));
    }

    public RMatrix subtract(RMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.map((row, column, oldValue) -> oldValue.subtract(other.get(row, column)));
    }

    public RMatrix subtractAndSet(RMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.mapAndSet((row, column, oldValue) -> oldValue.subtract(other.get(row, column)));
    }

    public RMatrix multiply(RMatrix other) {
        if(this.getColumnCount() != other.getRowCount()) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        RVector[] rows = this.getRows();
        RVector[] columns = other.getColumns();
        return new RMatrix(rows.length, columns.length, (row, column) -> rows[row].dot(columns[column]));
    }

    public RMatrix multiplyAndSet(RMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Multiplying mutable matrix with disallowed dimensions");
        }

        RVector[] rows = this.getRows();
        RVector[] columns = other.getColumns();
        return this.mapAndSet((row, column, oldValue) -> rows[row].dot(columns[column]));
    }

    public RVector multiply(RVector vector) {
        return vector.multiply(this);
    }

    public RVector multiplyAndSet(RVector vector) {
        return vector.multiplyAndSet(this);
    }

    public RMatrix multiply(Real scalar) {
        return this.map((row, column, oldValue) -> this.get(row, column).multiply(scalar));
    }

    public RMatrix multiplyAndSet(Real scalar) {
        return this.mapAndSet((row, column, oldValue) -> this.get(row, column).multiply(scalar));
    }

    public RMatrix divide(Real scalar) {
        return this.map((row, column, oldValue) -> this.get(row, column).divide(scalar));
    }

    public RMatrix divideAndSet(Real scalar) {
        return this.mapAndSet((row, column, oldValue) -> this.get(row, column).divide(scalar));
    }

    public RMatrix invert() {
        return this.luDecompose().getInverse();
    }

    public RMatrix invertAndSet() {
        RMatrix inverse = this.invert();
        return this.mapAndSet((row, column, oldValue) -> inverse.get(row, column));
    }

    public Real getDeterminant() {
        return this.luDecompose().getDeterminant();
    }

    public LUDecomposition.R luDecompose() {
        return LUDecomposition.of(this);
    }

    public RMatrix sub(int r1, int c1, int rowCount, int columnCount) {
        return new RMatrix.View(rowCount, columnCount,
                (row, column) -> this.get(r1 + row, c1 + column),
                (row, column, value) -> this.set(r1 + row, c1 + column, value));
    }

    public RMatrix subCopy(int r1, int c1, int rowCount, int columnCount) {
        return this.sub(r1, c1, rowCount, columnCount).copy();
    }

    public RMatrix.Augmented mergeToAugmented(RMatrix extra) {
        if(this.getRowCount() != extra.getRowCount()) {
            throw new UnsupportedOperationException("Merging two matrices with different row count");
        }

        return new RMatrix.Augmented(this, extra);
    }

    public RMatrix.Augmented splitToAugmented(int columnSplit) {
        return new RMatrix.Augmented(this, columnSplit);
    }

    public RMatrix copy() {
        return new RMatrix(this.getRowCount(), this.getColumnCount(), this.toGenerator());
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
        if(!(other instanceof RMatrix))return false;
        RMatrix matrix = (RMatrix)other;
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
        RVector[] rows = this.getRows();

        for(int i = 0; i < rows.length; i++) {
            sb.append(rows[i].toString()).append(i < rows.length - 1 ? "\n" : "");
        }

        return sb.toString();
    }

    public static class View extends RMatrix {
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
        public Real get(int row, int column) {
            return this.getter.getValue(row, column);
        }

        @Override
        public RMatrix set(int row, int column, Real value) {
            this.setter.set(row, column, value);
            return this;
        }

        @FunctionalInterface
        public interface Setter {
            void set(int row, int column, Real value);
        }
    }

    public static class Augmented extends RMatrix {
        private final RMatrix base;
        private final RMatrix extra;
        private final int split;

        public Augmented(RMatrix base, RMatrix extra) {
            super(0, 0);
            this.base = base;
            this.extra = extra;
            this.split = base.getColumnCount();
        }

        public Augmented(RMatrix merged, int split) {
            this(merged.sub(0, 0, merged.getRowCount() - 1, split - 1),
                    merged.sub(0, 0, merged.getRowCount() - 1, split - 1));
        }

        public RMatrix getBaseMatrix() {
            return this.base;
        }

        public RMatrix getExtraMatrix() {
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
        public Real get(int row, int column) {
            return column < this.getSplit() ? this.getBaseMatrix().get(row, column)
                    : this.getExtraMatrix().get(row, column - this.getSplit());
        }

        @Override
        public RMatrix set(int row, int column, Real value) {
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
        Real getValue(int row, int column);

        default RVector.Generator forRow(int row) {
            return index -> this.getValue(row, index);
        }

        default RVector.Generator forColumn(int column) {
            return index -> this.getValue(index, column);
        }

        default Mapper asMapper() {
            return (row, column, oldValue) -> this.getValue(row, column);
        }
    }

    @FunctionalInterface
    public interface Mapper {
        Real getNewValue(int row, int column, Real oldValue);

        default RVector.Mapper forRow(int row) {
            return (index, oldValue) -> this.getNewValue(row, index, oldValue);
        }

        default RVector.Mapper forColumn(int column) {
            return (index, oldValue) -> this.getNewValue(index, column, oldValue);
        }

        default Generator asGenerator() {
            return (row, column) -> this.getNewValue(row, column, null);
        }
    }

}
